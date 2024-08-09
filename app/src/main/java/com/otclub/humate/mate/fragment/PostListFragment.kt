package com.otclub.humate.mate.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.otclub.humate.BuildConfig.GENDER
import com.otclub.humate.BuildConfig.TEST_MEMBER
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostListBinding
import com.otclub.humate.mate.adapter.PostListAdapter
import com.otclub.humate.mate.data.LocalizedTag
import com.otclub.humate.mate.data.PostListFilterDTO
import com.otclub.humate.mate.data.Tag
import com.otclub.humate.mate.viewmodel.PostViewModel
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon


class PostListFragment : Fragment() {

    private var mBinding : MateFragmentPostListBinding? = null
    private val binding get() = mBinding!!
    private val selectedButtons = mutableSetOf<Button>() // 현재 선택된 버튼들을 추적
    private val selectedButtonsId = mutableSetOf<Int>()

    private lateinit var postViewModel: PostViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var postListAdapter: PostListAdapter

    private var tagName: String = ""
    private var keyword: String? = ""

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton

    private var filters = mutableMapOf(
        "gender" to GENDER,
        "memberId" to TEST_MEMBER
        "gender" to "m"
        // 초기 필터 값 설정
    )

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostListBinding.inflate(inflater, container, false)
        recyclerView = binding.postList
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ViewModel 초기화
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        mBinding = binding

        // searchInput과 searchButton 초기화
        searchInput = binding.searchInput
        searchButton = binding.searchButton

        // memberId 가져오기
        postViewModel.fetchMemberId(
            onSuccess = { memberId ->
                filters["memberId"] = memberId
                Log.i("memberId", memberId)
            },
            onError = { errorMessage ->
                // 오류 메시지를 표시하거나 로그로 남길 수 있습니다.
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // memberId 가져오기
        postViewModel.fetchMemberId(
            onSuccess = { memberId ->
                filters["memberId"] = memberId
                Log.i("memberId", memberId)
            },
            onError = { errorMessage ->
                // 오류 메시지를 표시하거나 로그로 남길 수 있습니다.
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val currentLanguage = sharedPreferencesManager.getLanguage()

        // 글쓰기 버튼 클릭 이벤트 설정
        val writeButton: Button = binding.writeButton

        writeButton.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postListWriteFragment)
        }

        // ballon 말풍선
        val balloon = createBalloon(requireContext()) {
            setArrowSize(10)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(BalloonSizeSpec.WRAP)
            setArrowOrientation(ArrowOrientation.RIGHT)
            setText(getString(R.string.write_ballon))
            setTextColorResource(R.color.black)
            setTextSize(10f)
            setBackgroundColorResource(R.color.super_light_gray)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setLifecycleOwner(lifecycleOwner)
            setPadding(8)
        }

        writeButton.post {
            balloon.showAlignLeft(writeButton)
            balloon.dismissWithDelay(3000)
        }

        // ViewModel의 데이터 사용
        postViewModel.filterData?.let { filterData ->
            Log.i("filter", "Received filter data: $filterData")

            filterData.matchDate?.let { matchDate ->
                filters["matchDate"] = matchDate
            }
            filterData.matchBranch?.let { matchBranch ->
                filters["matchBranch"] = matchBranch
            }
            filterData.matchGender?.let { matchGender ->
                filters["matchGender"] = matchGender
            }
            filterData.matchLanguage?.let { matchLanguage ->
                filters["matchLanguage"] = matchLanguage
            }
            filterData.keyword?.let { keyword ->
                filters["keyword"] = keyword
            }
            filterData.tagName?.let { tagName ->
                filters["tagName"] = tagName
            }

            Log.i("filter", "Updated filters: $filters")
        }

        postViewModel.getPostList(filters, onSuccess = { postList ->
            postListAdapter = PostListAdapter(postList) { postId ->
                val action = PostListFragmentDirections.actionPostListFragmentToPostListDetailFragment(postId)
                findNavController().navigate(action)
            }
            recyclerView.adapter = postListAdapter
        }, onError = { errorMessage ->
            // 에러 처리
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.list)

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        // 필터 버튼 가져오기
        val filterButton: Button = binding.filterButton

        // 필터 버튼 클릭 이벤트 설정
        filterButton.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postListFilterFragment)
        }

        // buttonContainer 레이아웃을 가져오기
        val buttonContainer = binding.buttonContainer
        // view.findViewById<LinearLayout>(R.id.button_container)

        // 태그 데이터 설정
        val tags = listOf(
            // 쇼핑
            Tag(
                iconResId = R.drawable.mate_shopping,
                titleResId = R.string.shopping,
                buttons = listOf(
                    LocalizedTag.CLOTHING,
                    LocalizedTag.BEAUTY,
                    LocalizedTag.ACCESSORY,
                    LocalizedTag.FOOTWEAR
                )
            ),
            // 식사
            Tag(
                iconResId = R.drawable.mate_food,
                titleResId = R.string.meal,
                buttons = listOf(
                    LocalizedTag.KOREAN_FOOD,
                    LocalizedTag.JAPANESE_FOOD,
                    LocalizedTag.WESTERN_FOOD,
                    LocalizedTag.CHINESE_FOOD,
                    LocalizedTag.SNACK
                )
            ),
            // 행사
            Tag(
                iconResId = R.drawable.mate_event,
                titleResId = R.string.event,
                buttons = listOf(
                    LocalizedTag.POPUP_STORE,
                    LocalizedTag.EXHIBITION,
                    LocalizedTag.PERFORMANCE
                )
            )
        )

        tags.forEach { tag ->
            for (localizedTag in tag.buttons) {
                val button = Button(ContextThemeWrapper(requireContext(), R.style.TagButtonUnselected), null, R.style.TagButtonUnselected)
                val buttonTextLocalized = localizedTag.getName(currentLanguage).lowercase()
                button.text = buttonTextLocalized

                // 텍스트의 실제 너비 측정
                val paint = Paint()
                paint.textSize = button.textSize
                val textWidth = paint.measureText(buttonTextLocalized)

                // 여유 공간(패딩)을 포함한 버튼 너비 계산
                val padding = 100 // 좌우 패딩 (예: 32dp * 2)
                val buttonWidth = textWidth.toInt() + padding

                val params = LinearLayout.LayoutParams(
                    buttonWidth,
                    100
                )
                // 버튼 간의 간격을 설정 (예: 8dp)
                params.setMargins(16, 0, 16, 0)
                button.layoutParams = params
                button.gravity = Gravity.CENTER
//            button.setPadding(32, 16, 16, 32)
                button.setPadding(0, 0, 0, 0)
                button.setBackgroundResource(R.drawable.tag_button_unselected)
                button.setTextColor(resources.getColor(R.color.dark_gray, null))

                // 버튼 추가 후 초기 상태 설정
                // 선택된 버튼 상태로 설정
                filters["tagName"]?.let { tagName ->
                    val selectedTags = tagName.split(", ")
                    val localizedButtonText = if (currentLanguage != 1) {
                        // LocalizedTag에서 buttonText에 해당하는 태그 찾기
                        val foundLocalizedTag = LocalizedTag.values().find { it.id.toString() == buttonTextLocalized }
                        // koreanName으로 변경
                        foundLocalizedTag?.koreanName ?: buttonTextLocalized
                    } else {
                        buttonTextLocalized
                    }

                    if (selectedTags.contains(localizedButtonText)) {
                        button.setBackgroundResource(R.drawable.tag_button_selected)
                        button.setTextColor(resources.getColor(R.color.white, null))
                        selectedButtons.add(button)
                        getTagIdByName(localizedButtonText, currentLanguage)?.let { selectedButtonsId.add(it) }
                    }
                }

                // 버튼 클릭 이벤트 설정
                button.setOnClickListener {
                    handleButtonClick(button)
                }

                buttonContainer.addView(button)
            }
        }


        // 검색 버튼 클릭 이벤트 설정
        searchButton.setOnClickListener {
            performSearch()
        }
    }

    private fun getTagIdByName(tagName: String, currentLanguage: Int): Int? {
        return LocalizedTag.values().firstOrNull {
            when (currentLanguage) {
                1 -> it.koreanName == tagName // 한국어 설정
                else -> it.englishName == tagName // 영어 설정
            }
        }?.id
    }

    private fun handleButtonClick(clickedButton: Button) {
        Log.i("태그 버튼 선택", "clickedButton -> ${clickedButton.text}")

        val currentLanguage = sharedPreferencesManager.getLanguage()
        val tagName = clickedButton.text.toString()
        val tagId = getTagIdByName(tagName, currentLanguage)

        if (selectedButtonsId.contains(tagId)) {
            // 선택 해제
            clickedButton.setBackgroundResource(R.drawable.tag_button_unselected)
            clickedButton.setTextColor(resources.getColor(R.color.dark_gray, null))
            selectedButtonsId.remove(tagId)
        } else {
            // 선택
            clickedButton.setBackgroundResource(R.drawable.tag_button_selected)
            clickedButton.setTextColor(resources.getColor(R.color.white, null)) // 선택된 버튼의 글자색을 흰색으로 변경
            if (tagId != null) {
                selectedButtonsId.add(tagId)
            }
        }

        updatePostList()
    }

    private fun performSearch() {
        val input = searchInput.text.toString().trim()

        // keyword가 빈 문자열일 경우 null로 설정
        keyword = if (input.isEmpty()) null else input

        updatePostList()
    }

    private fun updatePostList() {
        // 전역 필터에 tagName 추가 또는 업데이트
        val koreanNames = selectedButtonsId.mapNotNull { id ->
            val tag = LocalizedTag.fromId(id)
            tag?.koreanName
        }
        val tagNamesString = koreanNames.joinToString(separator = ", ")
        filters["tagName"] = tagNamesString

        // 전역 필터에 keyword 추가 또는 업데이트
        if (keyword != null && keyword!!.isNotEmpty()) {
            filters["keyword"] = keyword!!
        } else {
            filters.remove("keyword")
        }

        postViewModel.updateFilterData(
            matchDate = filters["matchDate"],
            matchBranch = filters["matchBranch"]?.split(", ")?.toSet() ?: emptySet(),
            matchGender = filters["matchGender"],
            matchLanguage = filters["matchLanguage"]?.split(", ")?.toSet() ?: emptySet(),
            keyword = keyword.takeIf { !it.isNullOrBlank() },
            tagName = tagName.takeIf { it.isNotBlank() }
        )

        // 데이터 가져오기 및 Adapter 설정
        postViewModel.getPostList(filters, onSuccess = { postList ->
            postListAdapter = PostListAdapter(postList) { postId ->
                val action = PostListFragmentDirections.actionPostListFragmentToPostListDetailFragment(postId)
                findNavController().navigate(action)
            }
            recyclerView.adapter = postListAdapter
        }, onError = { errorMessage ->
            // 에러 처리
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}