package com.otclub.humate.mate.fragment

import android.app.AlertDialog
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostWriteBinding
import com.otclub.humate.mate.data.*
import com.otclub.humate.mate.viewmodel.PostWriteViewModel
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import java.util.*

class PostWriteFragment : Fragment()  {

    private var mBinding : MateFragmentPostWriteBinding? = null
    private val binding get() = mBinding!!

    private lateinit var postWriteViewModel: PostWriteViewModel

    private lateinit var storeItemsContainer: LinearLayout
    private lateinit var addStoreButton: Button

    private var requests = mutableMapOf(
        "memberId" to "0"
        // 초기 요청 값 설정
    )

    private lateinit var placeItemsContainer: List<PostWritePlaceRequestDTO>

    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText

    private val selectedButtons = mutableSetOf<Button>() // 현재 선택된 버튼들을 추적
    private val selectedTags = mutableMapOf<String, Int>()


    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostWriteBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postWriteViewModel = ViewModelProvider(requireActivity()).get(PostWriteViewModel::class.java)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        mBinding = binding

        // memberId 가져오기
        postWriteViewModel.fetchMemberId(
            onSuccess = { memberId ->
                requests["memberId"] = memberId
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
        postWriteViewModel.fetchMemberId(
            onSuccess = { memberId ->
                requests["memberId"] = memberId
                Log.i("memberId", memberId)
            },
            onError = { errorMessage ->
                // 오류 메시지를 표시하거나 로그로 남길 수 있습니다.
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        // ViewModel의 데이터 사용
        postWriteViewModel.optionData?.let { optionData ->
            Log.i("PostWriteFragment", "Received option data: $optionData")

            optionData.matchDate?.let { matchDate ->
                requests["matchDate"] = matchDate
            }
            optionData.matchBranch?.let { matchBranch ->
                requests["matchBranch"] = matchBranch
            }
            optionData.matchGender?.let { matchGender ->
                requests["matchGender"] = matchGender.toString()
            }
            optionData.matchLanguage?.let { matchLanguage ->
                requests["matchLanguage"] = matchLanguage
            }
        }

        postWriteViewModel.requestData?.let { requestData ->
            Log.i("PostWriteFragment", "Received request data: $requestData")

            requestData.title?.let { title ->
                requests["title"] = title
            }
            requestData.content?.let { content ->
                requests["content"] = content
            }
            requestData.postPlaces.let { postPlaces ->
                requests["postPlaces"] = postPlaces.toString()
            }
            requestData.postTags.let { postTags ->
                requests["postTags"] = postTags.toString()
            }
        }

        Log.i("PostWriteFragment", "Received final data: $requests")

        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.write)

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

        // 매칭 정보 선택 버튼 가져오기
        val matchInfoButton: Button = binding.matchInfoButton

        // 매칭 정보 선택 버튼 클릭 이벤트 설정
        matchInfoButton.setOnClickListener {
            // requestDTO에 저장
            saveRequest()
            findNavController().navigate(R.id.action_postWriteFragment_to_postWriteOptionFragment)
        }

        // 작성하기 버튼 가져오기
        val postSaveButton: CardView = binding.postSaveButton

        // 작성하기 버튼 클릭 이벤트 설정
        postSaveButton.setOnClickListener {
            saveRequest()
            postWriteViewModel.writePost(
                onSuccess = { postId ->
                    // 성공 시 처리
                    Log.i("PostWrite", "Post created successfully with ID: $postId")

                    // 커스텀 다이얼로그 레이아웃 설정
                    val customView = layoutInflater.inflate(R.layout.mate_post_write_dialog, null)

                    // AlertDialog로 팝업 표시
                    AlertDialog.Builder(requireContext())
                        .setView(customView)
                        .setPositiveButton("확인") { dialog, which ->
                            // 확인 버튼 클릭 시 PostListFragment로 이동
                            findNavController().navigate(R.id.action_postWriteFragment_to_postListFragment)
                        }
                        .show()
                },
                onError = { errorMessage ->
                    // 실패 시 처리
                    Log.e("PostWrite", "Error occurred: $errorMessage")
                    Toast.makeText(context, "Failed to create post: $errorMessage", Toast.LENGTH_LONG).show()
                }
            )
        }

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
            addTagToLayout(tag)
        }

        storeItemsContainer = binding.storeItemsContainer
        addStoreButton = binding.addStoreButton
        placeItemsContainer = mutableListOf()

        addStoreButton.setOnClickListener {
            addStoreItem()
        }

        // 매칭글 작성 정보 저장하기
        updateBasicInfo()
        updateCardViews()

        initialize()

    }

    private fun getTagIdByName(tagName: String, currentLanguage: Int): Int? {
        return LocalizedTag.values().firstOrNull {
            when (currentLanguage) {
                1 -> it.koreanName == tagName // 한국어 설정
                else -> it.englishName == tagName // 영어 설정
            }
        }?.id
    }

    private fun parsePostWritePlaceRequestDTO(data: String): List<PostWritePlaceRequestDTO> {
        val places = mutableListOf<PostWritePlaceRequestDTO>()

        // 정규 표현식을 사용하여 문자열에서 type과 name 값을 추출
        val regex = """PostWritePlaceRequestDTO\(type=(\d+), name=([^)]*)\)""".toRegex()
        val matches = regex.findAll(data)

        for (match in matches) {
            val type = match.groupValues[1].toInt()
            val name = match.groupValues[2]
            places.add(PostWritePlaceRequestDTO(type, name))
        }

        return places
    }

    private fun parsePostWriteTagRequestDTO(data: String): List<PostWriteTagRequestDTO> {
        val tags = mutableListOf<PostWriteTagRequestDTO>()

        // 정규 표현식을 사용하여 문자열에서 tagId 값을 추출
        val regex = """PostWriteTagRequestDTO\(tagId=([^)]*)\)""".toRegex()
        val matches = regex.findAll(data)

        for (match in matches) {
            val tagId = match.groupValues[1].toInt()
            tags.add(PostWriteTagRequestDTO(tagId))
        }

        return tags
    }

    // 페이지 초기화
    private fun initialize() {
        // 제목
        Log.i("initialize", "title -> " + requests["title"] )
        titleInput = binding.titleInput
        titleInput.setText(requests["title"])

        // 내용
        contentInput = binding.contentInput
        contentInput.setText(requests["content"])

        // 매장 및 팝업스토어
        val postPlaces = requests["postPlaces"]?.let { parsePostWritePlaceRequestDTO(it) }

        if (postPlaces != null) {
            for (place in postPlaces) {
                initStoreItems(place)
            }
        }
    }

    // requestDTO 저장
    private fun saveRequest() {

        postWriteViewModel.updateRequestData(
            memberId = requests["memberId"],
            title = requests["title"],
            content = requests["content"],
            matchDate = requests["matchDate"],
            matchBranch = requests["matchBranch"]?.split(",")?.map { it.trim() }?.toSet()?: emptySet(),
            matchGender = requests["matchGender"]?.toIntOrNull(),
            matchLanguage = requests["matchLanguage"]?.split(",")?.map { it.trim() }?.toSet()?: emptySet(),
            postPlaces = placeItemsContainer,
            postTags = selectedTags.values.map { tagId ->
                PostWriteTagRequestDTO(tagId = tagId)
            }
        )


        Log.i("태그 등록 확인", "$selectedTags")
    }

    // 매칭글 작성 정보 저장하기
    // 1. 제목과 내용
    private fun updateBasicInfo() {
        // 제목
        titleInput = binding.titleInput
        titleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                requests["title"] = s.toString()
                Log.i("updateBasicInfo", "title바뀜 ->" + requests["title"])
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 내용
        contentInput = binding.contentInput
        contentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                requests["content"] = s.toString()
                Log.i("updateBasicInfo", "content바뀜 ->" + requests["content"])
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    // 2. 매칭 정보 선택
    private fun updateCardViews() {
        val currentLanguage = sharedPreferencesManager.getLanguage()

        postWriteViewModel.optionData?.let { optionData ->
            // 날짜 카드뷰 업데이트
            binding.card1.findViewById<TextView>(R.id.card1_text).text = optionData.matchDate ?: "-"

            // 지점 카드뷰 업데이트
            val matchBranchText = optionData.matchBranch?.let { koreanName ->
                val branch = LocalizedBranch.values().find { it.koreanName == koreanName }
                branch?.let {
                    if (currentLanguage == 1) {
                        it.koreanName // 한국어 이름을 반환
                    } else {
                        it.englishName // 영어 이름을 반환
                    }
                } ?: "-"
            } ?: "-"
            binding.card2.findViewById<TextView>(R.id.card2_text).text = matchBranchText

            // 성별 카드뷰 업데이트
            binding.card3.findViewById<TextView>(R.id.card3_text).text = optionData.matchGender?.let {
                when (it) {
                    1 -> context?.getString(R.string.same_gender)
                    2 -> context?.getString(R.string.both_gender)
                    else -> "-"
                }
            } ?: "-"

            // 언어 카드뷰 업데이트
            val matchLanguageText = optionData.matchLanguage ?: "-"

            val selectedLanguage = matchLanguageText.split(", ") ?: emptyList()
            val englishLanguage = selectedLanguage.map { getEnglishLanguageName(it) }
            val englishLanguageText = if (englishLanguage.isNotEmpty()) {
                englishLanguage.joinToString(", ")
            } else {
                "-"
            }
            binding.card4.findViewById<TextView>(R.id.card4_text).text =
                if (currentLanguage == 1) { matchLanguageText }
                else { englishLanguageText }
        }

    }

    fun getEnglishLanguageName(language: String): String {
        return when (language) {
            "한국어" -> "Korean"
            "영어" -> "English"
            "중국어" -> "Chinese"
            "일본어" -> "Japanese"
            else -> language
        }
    }

    // 3. 매장 및 팝업스토어 설정

    // 4. 태그 설정
    private fun addTagToLayout(tag: Tag) {
        val currentLanguage = sharedPreferencesManager.getLanguage()
        Log.i("태그 언어 변경", "$currentLanguage")
        val container = binding.tagContainer
        val postTags = requests["postTags"]?.let { parsePostWriteTagRequestDTO(it) }

        // 수평으로 아이콘, 제목, 버튼들을 배치할 LinearLayout 생성
        val categoryLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100
            ).apply {
                setMargins(0, 8.dpToPx(), 0, 0)
            }
            setPadding(8.dpToPx(), 0, 8.dpToPx(), 0)
            gravity = Gravity.CENTER_VERTICAL
        }

        // 아이콘을 위한 ImageView 생성
        val icon = ImageView(context).apply {
            setImageResource(tag.iconResId)
            layoutParams = LinearLayout.LayoutParams(
                14.dpToPx(),
                14.dpToPx()
            ).apply {
                setMargins(0, 0, 8.dpToPx(), 0)
            }
        }

        // 제목을 위한 TextView 생성
        val title = TextView(context).apply {
            text = context.getString(tag.titleResId)
            textSize = 13f
            setTextColor(resources.getColor(R.color.black, null))
            typeface = Typeface.DEFAULT_BOLD

            // 텍스트의 폭을 측정
            val textPaint = paint
            val textWidth = textPaint.measureText(text.toString())

            // 패딩을 포함한 최종 너비 계산
            val finalWidth = textWidth.toInt() + 40

            layoutParams = LinearLayout.LayoutParams(
                finalWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                setMargins(0, 0, 4.dpToPx(), 0)
            }
        }

        categoryLayout.addView(icon)
        categoryLayout.addView(title)

        // 버튼들을 수평으로 배치할 LinearLayout 생성
        val buttonContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        for (localizedTag in tag.buttons) {
            val buttonTextLocalized = localizedTag.getName(currentLanguage).lowercase()

            Log.i("태그 언어 변경", "buttonTextLocalized -> $buttonTextLocalized")

            val button = Button(ContextThemeWrapper(requireContext(), R.style.TagButtonUnselected), null, R.style.TagButtonUnselected).apply {
                text = buttonTextLocalized
                isAllCaps = false

                textSize = 11f

                // 텍스트의 실제 너비 측정
                val paint = Paint()
                paint.textSize = this.textSize
                val textWidth = paint.measureText(buttonTextLocalized)

                // 여유 공간(패딩)을 포함한 버튼 너비 계산
                val padding = 120 // 좌우 패딩
                val buttonWidth = textWidth.toInt() + padding

                val params = LinearLayout.LayoutParams(
                    buttonWidth,
                    100
                ).apply {
                    setMargins(14, 0, 14,0)
                }
                layoutParams = params
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundResource(R.drawable.tag_button_unselected)
                setTextColor(resources.getColor(R.color.dark_gray, null))
            }

            buttonContainer.addView(button)

            // 버튼 클릭 이벤트 설정
            button.setOnClickListener {
                handleButtonClick(button)
            }

            // postTags
            if (postTags != null) {
                for (postTag in postTags) {
                    if (postTag.tagId == getTagIdByName(buttonTextLocalized, currentLanguage)) {
                        Log.i("PostTag", "버튼 나타내기 postTag -> $postTag")
                        button.setBackgroundResource(R.drawable.tag_button_selected)
                        button.setTextColor(resources.getColor(R.color.white, null))
                        selectedButtons.add(button)
                    }
                }

            }
        }

        // 버튼들을 포함하는 buttonContainer를 categoryLayout에 추가
        categoryLayout.addView(buttonContainer)

        // 전체 콘텐츠를 수평으로 스크롤할 수 있는 HorizontalScrollView 생성
        val horizontalScrollView = HorizontalScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isHorizontalScrollBarEnabled = false
        }

        // categoryLayout을 HorizontalScrollView에 추가
        horizontalScrollView.addView(categoryLayout)

        // HorizontalScrollView를 container에 추가
        container.addView(horizontalScrollView)
    }

    // 태그 버튼 클릭 시
    fun handleButtonClick(button: Button) {
        val currentLanguage = sharedPreferencesManager.getLanguage()
        val tagName = button.text.toString()
        val tagId = getTagIdByName(tagName, currentLanguage)
        Log.i("태그 등록 확인", "tagName -> $tagName, tagId -> $tagId")

        if (selectedButtons.contains(button)) {
            // 이미 선택된 태그인 경우
            selectedButtons.remove(button)
            selectedTags.remove(tagName)
            button.setBackgroundResource(R.drawable.tag_button_unselected)
            button.setTextColor(resources.getColor(R.color.dark_gray, null))
        } else {
            // 선택되지 않은 태그인 경우
            selectedButtons.add(button)
            tagId?.let { selectedTags[tagName] = tagId }
            button.setBackgroundResource(R.drawable.tag_button_selected)
            button.setTextColor(resources.getColor(R.color.white, null))
        }
        Log.i("PostTag", "selectedTags -> $selectedTags")
    }

    // 장소 및 팝업스토어 초기화할 때
    private fun initStoreItems(place: PostWritePlaceRequestDTO) {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.mate_item_store, storeItemsContainer, false)

        val spinner: Spinner = itemView.findViewById(R.id.select_spinner)
        val textView: TextView = itemView.findViewById(R.id.selected_type)
        val deleteButton: Button = itemView.findViewById(R.id.place_delete_button)
        val contentInput: EditText = itemView.findViewById(R.id.content_input)

        // name 설정
        contentInput.setText(place.name)

        // Spinner의 항목 설정
        val options = arrayOf(getString(R.string.store), getString(R.string.pop_up))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinner.adapter = adapter

        // 선택 항목 설정
        textView.text = when (place.type) {
            1 -> getString(R.string.store)
            2 -> getString(R.string.pop_up)
            else -> ""
        }

        deleteButton.setOnClickListener {
            storeItemsContainer.removeView(itemView)

            // placeItemsContainer에 place 삭제
            placeItemsContainer = placeItemsContainer.toMutableList().apply {
                remove(place)
            }
        }

        placeItemsContainer = placeItemsContainer.toMutableList().apply {
            add(place)
        }

        storeItemsContainer.addView(itemView)
    }

    // 장소 및 팝업스토어 추가 버튼 눌렀을 때
    private fun addStoreItem() {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.mate_item_store, storeItemsContainer, false)

        val spinner: Spinner = itemView.findViewById(R.id.select_spinner)
        val textView: TextView = itemView.findViewById(R.id.selected_type)
        val deleteButton: Button = itemView.findViewById(R.id.place_delete_button)
        val saveButton: Button = itemView.findViewById(R.id.place_save_button)
        val contentInput: EditText = itemView.findViewById(R.id.content_input)

        // new_place 초기화
        var new_place = PostWritePlaceRequestDTO(type = 1, name = "")

        // new_place.name 설정
        contentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                new_place = new_place.copy(name = s.toString())
                Log.i("storeData", "바뀜! -> $s")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Spinner의 항목 설정
        val options = arrayOf(getString(R.string.store), getString(R.string.pop_up))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(R.layout.mate_spinner_item)
        spinner.adapter = adapter

        // Spinner의 아이템 선택 리스너 설정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                textView.text = selectedItem
                // new_place.type 설정
                new_place.type = when (selectedItem) {
                    getString(R.string.store) -> 1
                    getString(R.string.pop_up) -> 2
                    else -> 1
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // 삭제 버튼 클릭 리스너 설정
        deleteButton.setOnClickListener {
            storeItemsContainer.removeView(itemView)

            // placeItemsContainer에 newPlace 삭제
            placeItemsContainer = placeItemsContainer.toMutableList().apply {
                remove(new_place)
            }
        }

        // 저장 버튼 클릭 리스너 설정
        saveButton.setOnClickListener {
            contentInput.isEnabled = false
            contentInput.isFocusable = false
            contentInput.isFocusableInTouchMode = false
            spinner.isEnabled = false
            saveButton.isEnabled = false

            // placeItemsContainer에 newPlace 추가
            placeItemsContainer = placeItemsContainer.toMutableList().apply {
                add(new_place)
            }
            Log.i("storeData", "update data in fragment -> $new_place")
        }

        storeItemsContainer.addView(itemView)
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    // 작성 완료 버튼 클릭 시

}