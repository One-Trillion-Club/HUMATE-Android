package com.otclub.humate.mate.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.viewModel.ChatViewModel
import com.otclub.humate.common.LoadingDialog
import com.otclub.humate.databinding.MateFragmentPostDetailBinding
import com.otclub.humate.mate.adapter.PostDetailAdapter
import com.otclub.humate.mate.adapter.PostListAdapter
import com.otclub.humate.mate.data.LocalizedBranch
import com.otclub.humate.mate.data.LocalizedTag
import com.otclub.humate.mate.data.PostDetailResponseDTO
import com.otclub.humate.mate.viewmodel.PostDetailViewModel
import com.otclub.humate.member.viewmodel.MemberViewModel
import com.otclub.humate.sharedpreferences.SharedPreferencesManager

class PostDetailFragment : Fragment() {

    private var mBinding : MateFragmentPostDetailBinding? = null
    private val binding get() = mBinding!!
    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var postDetailViewModel: PostDetailViewModel
    private lateinit var postDetailAdapter: PostDetailAdapter

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val memberViewModel: MemberViewModel by activityViewModels()
    private val chatViewModel : ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostDetailBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postDetailViewModel = ViewModelProvider(requireActivity()).get(PostDetailViewModel::class.java)

        // ChatViewModel 초기화

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        mBinding = binding

        return mBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.detail)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 뒤로가기 버튼 이벤트 처리
            leftButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }


        val postId = args.postId

        // 상세 게시글 정보 요청
        postDetailViewModel.getPostDetail(postId, onSuccess = { postDetail ->
            updateUI(postDetail)
            binding.profileImage.setOnClickListener {
                memberViewModel.getOtherMemberProfile(
                    memberId = postDetail.memberId,
                    onSuccess = { profile ->
                        val loadingDialog = LoadingDialog(requireContext())
                        loadingDialog.showMateDetailPopup(profile)
                    },
                    onError = { error ->
                        Toast.makeText(context, R.string.toast_please_one_more_time, Toast.LENGTH_SHORT).show()
                    })
            }
        }, onError = { errorMessage ->
            // 에러 처리
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

    }

    private fun getEnglishLanguageName(language: String): String {
        return when (language) {
            "한국어" -> "Korean"
            "영어" -> "English"
            "중국어" -> "Chinese"
            "일본어" -> "Japanese"
            else -> language
        }
    }

    private fun updateUI(postDetail: PostDetailResponseDTO) {
        val currentLanguage = sharedPreferencesManager.getLanguage()

        // 프로필 이미지 설정
        Glide.with(this)
            .load(postDetail.profileImgUrl) // URL
            .apply(RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_member_profile_default)
            )
            .into(binding.profileImage.findViewById(R.id.rounded_image)) // ImageView에 로드

        // 게시글 제목과 내용 설정
        binding.postTitle.text = postDetail.title
        binding.postContent.text = postDetail.content

        // 매칭 정보 업데이트 (CardView의 내용)
        val matchDateText = postDetail.matchDate ?: "-"
        val matchBranchText = postDetail.matchBranch?.let { koreanName ->
            val branch = LocalizedBranch.values().find { it.koreanName == koreanName }
            branch?.let {
                if (currentLanguage == 1) {
                    it.koreanName // 한국어 이름을 반환
                } else {
                    it.englishName // 영어 이름을 반환
                }
            } ?: "-"
        } ?: "-"
        val matchLanguageText = postDetail.matchLanguage ?: "-"

        val selectedLanguage = matchLanguageText.split(", ") ?: emptyList()
        val englishLanguage = selectedLanguage.map { getEnglishLanguageName(it) }
        val englishLanguageText = if (englishLanguage.isNotEmpty()) {
            englishLanguage.joinToString(", ")
        } else {
            "-"
        }

        val matchGenderText = when (postDetail.matchGender) {
            1 -> context?.getString(R.string.same_gender)
            2 -> context?.getString(R.string.both_gender)
            else -> context?.getString(R.string.both_gender)
        }
        binding.card1.findViewById<TextView>(R.id.card1_text).text = matchDateText
        binding.card2.findViewById<TextView>(R.id.card2_text).text = matchBranchText
        binding.card3.findViewById<TextView>(R.id.card3_text).text = matchGenderText
        binding.card4.findViewById<TextView>(R.id.card4_text).text =
            if (currentLanguage == 1) { matchLanguageText }
            else { englishLanguageText }

        fun adjustTextSize(textView: TextView) {
            textView.textSize = if (currentLanguage != 1) {
                9f // 필요에 따라 조정 (단위: sp)
            } else {
                11f // 기본 텍스트 사이즈 (단위: sp)
            }
        }

        adjustTextSize(binding.card1.findViewById(R.id.card1_text))
        adjustTextSize(binding.card2.findViewById(R.id.card2_text))
        adjustTextSize(binding.card3.findViewById(R.id.card3_text))
        adjustTextSize(binding.card4.findViewById(R.id.card4_text))

        // 매장 및 팝업스토어 업데이트
        val placeContainerLayout: LinearLayout = binding.placeContainerLayout
        placeContainerLayout.removeAllViews() // 기존 뷰 제거

        postDetail.postPlaces.forEach { place ->
            val placeView = LayoutInflater.from(context).inflate(R.layout.mate_item_place, placeContainerLayout, false)


            val context = placeView.context

            val typeText = when (place.type) {
                1 -> context.getString(R.string.store)   // 매장
                2 -> context.getString(R.string.pop_up)  // 팝업스토어
                else -> "-"    // 알 수 없음
            }
            val nameText = place.name ?: "-"

            val placeType: TextView = placeView.findViewById<TextView>(R.id.place_type)
            val placeName: TextView = placeView.findViewById<TextView>(R.id.place_name)

            placeType.text = typeText
            placeName.text = nameText

            placeContainerLayout.addView(placeView)
        }

        // 태그 업데이트
        val tagContainerLayout: LinearLayout = binding.tagContainerLayout
        tagContainerLayout.removeAllViews() // 기존 태그 제거

        postDetail.postTags.forEach { tag ->
            val tagView = TextView(context).apply {
                val displayText = if (currentLanguage != 1) {
                    val localizedTag = LocalizedTag.values().find { it.koreanName == tag.name }
                    localizedTag?.englishName ?: tag
                } else {
                    tag.name
                }

                text = displayText.toString()
                textSize = 10f
                setPadding(40, 8, 40, 8) // 패딩을 추가하여 양 끝 여백을 확보
                setBackgroundResource(R.drawable.mate_post_detail_tag_background)
                setTextColor(resources.getColor(R.color.humate_main, null)) // 텍스트 색상 설정
                typeface = Typeface.DEFAULT_BOLD // 텍스트를 볼드체로 설정
                gravity = Gravity.CENTER // 텍스트를 가운데 정렬

                // 텍스트의 폭을 측정
                val textPaint = paint
                val textWidth = textPaint.measureText(text.toString())

                // 패딩을 포함한 최종 너비 계산
                val finalWidth = textWidth.toInt() + 80

                layoutParams = LinearLayout.LayoutParams(
                    finalWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 태그 간의 간격을 조정
                    marginEnd = 30 // 태그 간의 끝 여백
                }
            }

            // LinearLayout에 추가
            tagContainerLayout.addView(tagView)
        }


    }
}