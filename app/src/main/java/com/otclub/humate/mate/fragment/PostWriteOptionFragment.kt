package com.otclub.humate.mate.fragment

import android.app.DatePickerDialog
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostWriteOptionBinding
import com.otclub.humate.mate.data.LocalizedBranch
import com.otclub.humate.mate.data.LocalizedTag
import com.otclub.humate.mate.viewmodel.PostWriteViewModel
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class PostWriteOptionFragment : Fragment() {

    private var mBinding : MateFragmentPostWriteOptionBinding? = null
    private val binding get() = mBinding!!

    private lateinit var postWriteViewModel: PostWriteViewModel

    private var options = mutableMapOf<String, String>()
    private var selectedDate: String? = null
    private val selectedBranchesId = mutableSetOf<Int>()
    private val selectedBranchesName = mutableSetOf<String>()
    private var selectedGender: Int? = null
    private var selectedLanguageKName = mutableSetOf<String>()
    private var selectedLanguageFName = mutableSetOf<String>()

    private var selectedGenderButton: CardView? = null
    private var selectedLanguageButton = mutableSetOf<Button>()

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    val englishToKorean = mutableMapOf(
        "Korean" to "한국어",
        "English" to "영어",
        "Japanese" to "일본어",
        "Chinese" to "중국어"
    )

    val koreanToEnglish = mutableMapOf(
        "한국어" to "Korean",
        "영어" to "English",
        "일본어" to "Japanese",
        "중국어" to "Chinese"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostWriteOptionBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postWriteViewModel = ViewModelProvider(requireActivity()).get(PostWriteViewModel::class.java)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        mBinding = binding

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentLanguage = sharedPreferencesManager.getLanguage()

        // ViewModel의 데이터 사용
        postWriteViewModel.optionData?.let { optionData ->
            Log.i("option", "Received option data: $optionData")

            optionData.matchDate?.let { matchDate ->
                options["matchDate"] = matchDate
            }
            optionData.matchBranch?.let { matchBranch ->
                options["matchBranch"] = matchBranch
            }
            optionData.matchGender?.let { matchGender ->
                options["matchGender"] = matchGender.toString()
            }
            optionData.matchLanguage?.let { matchLanguage ->
                options["matchLanguage"] = matchLanguage
            }
            Log.i("option", "Updated options: $options")
        }

        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.write_detail)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 확인 버튼 이벤트 처리
            rightButton.setOnClickListener {
                for (branchId in selectedBranchesId) {
                    val branch = LocalizedBranch.fromId(branchId)
                    branch?.let {
                        val name = it.koreanName
                        Log.i("haha", "저장하기 -> $name")
                        selectedBranchesName.add(name)
                    }
                }

                postWriteViewModel.updateOptionData(
                    matchDate = selectedDate.takeIf { !it.isNullOrBlank()},
                    matchBranch = selectedBranchesName,
                    matchGender = selectedGender,
                    matchLanguage = selectedLanguageKName
                )
                findNavController().navigate(R.id.action_postWriteOptionFragment_to_postWriteFragment)
            }

            // 뒤로가기 버튼 이벤트 처리
            leftButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        // buttonContainer 레이아웃을 가져오기
        val buttonContainer = binding.branchSelectionsContainer

        // 지점 데이터 설정
        val branches = listOf(
            LocalizedBranch.THE_HYUNDAI_SEOUL,
            LocalizedBranch.THE_HYUNDAI_DAEGU,
            LocalizedBranch.APGUJEONG,
            LocalizedBranch.TRADE_CENTER,
            LocalizedBranch.CHEONHO,
            LocalizedBranch.SINCHON,
            LocalizedBranch.MIA,
            LocalizedBranch.MOKDONG,
            LocalizedBranch.JUNGDONG,
            LocalizedBranch.KINTEX,
            LocalizedBranch.D_CUBE,
            LocalizedBranch.PANGYO,
            LocalizedBranch.BUSAN,
            LocalizedBranch.ULSAN,
            LocalizedBranch.ULSAN_DONG_GU,
            LocalizedBranch.CHUNGCHEONG
        )

        for (branch in branches) {
            val buttonTextLocalized = branch.getName(currentLanguage).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            Log.i("지점 변경", "buttonTextLocalized -> $buttonTextLocalized")

            val button = Button(ContextThemeWrapper(requireContext(), R.style.MatchBranchFilterUnselected), null, R.style.MatchBranchFilterUnselected).apply {
                text = buttonTextLocalized

                // 텍스트의 실제 너비 측정
                val paint = Paint()
                paint.textSize = this.textSize
                val textWidth = paint.measureText(buttonTextLocalized)

                // 여유 공간(패딩)을 포함한 버튼 너비 계산
                val padding = 100 // 좌우 패딩 (예: 32dp * 2)
                val buttonWidth = textWidth.toInt() + padding

                val params = LinearLayout.LayoutParams(
                    buttonWidth,
                    90
                )

                // 버튼 간의 간격을 설정
                params.setMargins(12, 0, 12, 0)
                layoutParams = params
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundResource(R.drawable.ic_tag_choice_long)
                setTextColor(resources.getColor(R.color.filter_tag_text, null))
            }

            buttonContainer.addView(button)

            // 버튼 클릭 이벤트 설정
            button.setOnClickListener {
                handleButtonClick(button)
            }
        }

        // 날짜 선택 버튼 클릭 이벤트 설정
        val datePickerButton: LinearLayout = binding.dateSelection
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        // 날짜 선택 초기화 버튼 클릭 이벤트 설정
        val dateResetButton: Button = binding.dateResetButton
        dateResetButton.setOnClickListener {
            resetDateSelection()
        }

        // 지점 선택 초기화 버튼 클릭 이벤트 설정
        val branchResetButton: Button = binding.branchResetButton
        branchResetButton.setOnClickListener {
            resetBranchSelection()
        }

        // 성별 버튼 클릭 이벤트 설정
        setupGenderButtons()

        // 성별 초기화 버튼 클릭 이벤트 설정
        val genderResetButton: Button = binding.genderResetButton
        genderResetButton.setOnClickListener {
            resetGenderSelection()
        }

        // 언어 버튼 클릭 이벤트 설정
        setupLanguageButtons()

        // 언어 버튼 클릭 이벤트 설정
        val languageResetButton: Button = binding.languageResetButton
        languageResetButton.setOnClickListener {
            resetLanguageSelection()
        }

        // 옵션 정보에 따라 버튼 상태 초기화
        initializeOptions()
    }

    // 초기화
    private fun initializeOptions() {
        val currentLanguage = sharedPreferencesManager.getLanguage()

        Log.i("haha", "초기화")
        // 날짜 초기화
        options["matchDate"]?.let { matchDate ->
            selectedDate = matchDate
            val dateFormat = SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREA)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(matchDate)
            val formattedDate = date?.let { dateFormat.format(it) } ?: ""
            mBinding?.dateSelectionText?.apply {
                text = formattedDate
                setTextColor(resources.getColor(R.color.nav_item_text_color_selected, null))
                setTypeface(null, Typeface.BOLD)
            }
        }

        // 지점 초기화
        options["matchBranch"]?.let { matchBranch ->
            Log.i("haha", "$matchBranch")
            val currentLanguage = sharedPreferencesManager.getLanguage()
            selectedBranchesId.clear()
            matchBranch.split(", ").forEach { branch ->
                val branchId = LocalizedBranch.fromName(branch)?.id
                Log.i("haha", "$branch")
                Log.i("haha", "$branchId")
                if (branchId != null) {
                    selectedBranchesId.add(branchId)
                    Log.i("haha", "$selectedBranchesId")
                }
                // 선택된 버튼 스타일 적용
                setButtonSelectedState(branch, true)
            }
        }

        // 성별 초기화
        options["matchGender"]?.let { matchGender ->
            when (matchGender) {
                "1" -> updateGenderButtonStates(mBinding?.gender1 ?: return, "1")
                "2" -> updateGenderButtonStates(mBinding?.gender2 ?: return, "2")
            }
        }

        // 언어 초기화
        options["matchLanguage"]?.let { matchLanguage ->
            selectedLanguageKName.clear()
            matchLanguage.split(", ").forEach { language ->
                selectedLanguageKName.add(language)

                if (currentLanguage == 1) {
                    koreanToEnglish[language]?.let { selectedLanguageFName.add(it) }
                }

                // 선택된 버튼 스타일 적용
                setLanguageButtonSelectedState(language, true)
            }
        }
    }

    private fun setButtonSelectedState(branch: String, isSelected: Boolean) {
        val currentLanguage = sharedPreferencesManager.getLanguage()

        // branch를 currentLanguage에 따라 변환
        val displayBranch = if (currentLanguage == 1) {
            // 현재 언어가 1 (한국어)일 때
            branch
        } else {
            // 현재 언어가 1이 아닐 때 (영어)
            val localizedTag = LocalizedTag.values().find { it.koreanName == branch }
            localizedTag?.englishName ?: branch
        }

        Log.i("haha", "displayBranch -> $displayBranch")
        // 버튼 컨테이너에서 해당 지점 버튼을 찾아 상태를 설정
        val buttonContainer = mBinding?.branchSelectionsContainer
        buttonContainer?.children?.filterIsInstance<Button>()?.forEach { button ->
            if (button.text.toString() == displayBranch) {
                if (isSelected) {
                    button.setBackgroundResource(R.drawable.ic_tag_choice_long_selected)
                    button.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    button.setBackgroundResource(R.drawable.ic_tag_choice_long)
                    button.setTextColor(resources.getColor(R.color.filter_tag_text, null))
                }
            }
        }
    }

    private fun setLanguageButtonSelectedState(language: String, isSelected: Boolean) {
        // 버튼을 찾아 상태를 설정
        val button = when (language) {
            context?.getString(R.string.korean) -> mBinding?.koreanButton
            context?.getString(R.string.english) -> mBinding?.englishButton
            context?.getString(R.string.japanese)  -> mBinding?.japaneseButton
            context?.getString(R.string.chinese)  -> mBinding?.chineseButton
            else -> null
        }

        button?.let {
            if (isSelected) {
                it.setBackgroundResource(R.drawable.ic_tag_choice_short_selected)
                it.setTextColor(resources.getColor(R.color.white, null))
            } else {
                it.setBackgroundResource(R.drawable.ic_tag_choice_short)
                it.setTextColor(resources.getColor(R.color.filter_tag_text, null))
            }
        }
    }

    // 날짜 선택
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->

                // 선택된 날짜를 처리
                val date = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }

                // 날짜 저장
                val selectedDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                selectedDate = selectedDateFormat.format(date.time)

                val dateFormat = SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREA)
                val formattedDate = dateFormat.format(date.time)

                val dateTextView: TextView? = mBinding?.dateSelectionText
                dateTextView?.text = formattedDate

                // 글씨 색상 및 스타일 변경
                dateTextView?.setTextColor(resources.getColor(R.color.nav_item_text_color_selected, null))
                dateTextView?.setTypeface(null, Typeface.BOLD)

                Toast.makeText(context, "선택된 날짜: $formattedDate", Toast.LENGTH_SHORT).show()
            }, year, month, day)

        datePickerDialog.show()
    }

    // 날짜 초기화
    private fun resetDateSelection() {
        // 선택한 날짜 없애고 기본 텍스트 출력
        val defaultDate = "날짜를 선택해주세요" // 기본 텍스트
        val dateTextView: TextView? = mBinding?.dateSelectionText
        dateTextView?.text = defaultDate

        // 텍스트 색상 및 스타일 초기화
        dateTextView?.setTextColor(resources.getColor(R.color.input_text_gray, null))
        dateTextView?.setTypeface(null, Typeface.NORMAL)

        selectedDate = null
    }

    private fun getBranchIdByName(branchName: String, currentLanguage: Int): Int? {
        return LocalizedBranch.values().firstOrNull {
            when (currentLanguage) {
                1 -> it.koreanName == branchName // 한국어 설정
                else -> it.englishName == branchName // 영어 설정
            }
        }?.id
    }

    // 지점 선택
    private fun handleButtonClick(clickedButton: Button) {
        val currentLanguage = sharedPreferencesManager.getLanguage()
        val branchName = clickedButton.text.toString()
        val branchId = getBranchIdByName(branchName, currentLanguage)
        Log.i("지점 등록 확인", "branchName -> $branchName, branchId -> $branchId")

        // 버튼 스타일 변경
        if (selectedBranchesId.contains(branchId)) {
            // 이미 선택된 버튼이면 선택 해제
            clickedButton.setBackgroundResource(R.drawable.ic_tag_choice_long)
            clickedButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))
            selectedBranchesId.remove(branchId)
        } else {
            // 새로 선택된 버튼이면 강조
            clickedButton.setBackgroundResource(R.drawable.ic_tag_choice_long_selected)
            clickedButton.setTextColor(resources.getColor(R.color.white, null))
            if (branchId != null) {
                selectedBranchesId.add(branchId)
            }
        }
    }

    // 지점 초기화
    private fun resetBranchSelection() {
        val currentLanguage = sharedPreferencesManager.getLanguage()

        // 버튼 컨테이너에서 모든 버튼을 가져옴
        val buttonContainer = mBinding?.branchSelectionsContainer
        val buttons = buttonContainer?.children?.filterIsInstance<Button>() ?: return

        // 모든 버튼의 배경과 텍스트 색상 초기화
        buttons.forEach { button ->
            val branchName = button.text.toString()
            val branchId = getBranchIdByName(branchName, currentLanguage)
            button.setBackgroundResource(R.drawable.ic_tag_choice_long)
            button.setTextColor(resources.getColor(R.color.filter_tag_text, null))
            selectedBranchesId.remove(branchId)
        }
    }

    // 성별 선택
    private fun setupGenderButtons() {
        val gender1: CardView = mBinding?.gender1 ?: return
        val gender2: CardView = mBinding?.gender2 ?: return

        gender1.setOnClickListener {
            updateGenderButtonStates(gender1, "1")
        }

        gender2.setOnClickListener {
            updateGenderButtonStates(gender2, "2")
        }
    }

    private fun updateGenderButtonStates(selectedCard: CardView, gender: String) {
        val gender1: CardView = mBinding?.gender1 ?: return
        val gender2: CardView = mBinding?.gender2 ?: return

        // 현재 선택된 버튼과 다른 버튼을 업데이트
        val unselectedCard = if (selectedCard == gender1) gender2 else gender1

        selectedCard.setBackgroundResource(R.drawable.mate_post_write_gender_background)
        unselectedCard.setBackgroundResource(R.color.white)

        // 선택된 버튼 업데이트
        selectedGenderButton = selectedCard

        // 성별 저장
        selectedGender = gender.toInt()
    }

    // 성별 초기화
    private fun resetGenderSelection() {
        val gender1: CardView = mBinding?.gender1 ?: return
        val gender2: CardView = mBinding?.gender2 ?: return

        gender1.setBackgroundResource(R.color.white)
        gender2.setBackgroundResource(R.color.white)

        // 선택된 버튼 초기화
        selectedGenderButton = null
        selectedGender = null
    }

    // 언어 선택
    private fun setupLanguageButtons() {
        val koreanButton: Button = mBinding?.koreanButton ?: return
        val englishButton: Button = mBinding?.englishButton ?: return
        val japaneseButton: Button = mBinding?.japaneseButton ?: return
        val chineseButton: Button = mBinding?.chineseButton ?: return

        koreanButton.setOnClickListener {
            updateLanguageButtonStates(koreanButton, "한국어")
        }

        englishButton.setOnClickListener {
            updateLanguageButtonStates(englishButton, "영어")
        }

        japaneseButton.setOnClickListener {
            updateLanguageButtonStates(japaneseButton, "일본어")
        }

        chineseButton.setOnClickListener {
            updateLanguageButtonStates(chineseButton, "중국어")
        }
    }

    private fun updateLanguageButtonStates(selectedButton: Button, language: String) {
        // 버튼이 이미 선택된 상태인지 확인
        val isAlreadySelected = selectedButton in selectedLanguageButton
        val currentLanguage = sharedPreferencesManager.getLanguage()

        val originalText = selectedButton.text
        val buttonText = if (currentLanguage == 1) {
            originalText
        } else {
            englishToKorean[originalText]
        }

        Log.i("언어 선택", "$originalText")
        if (isAlreadySelected) {
            // 선택 상태를 취소
            selectedButton.setBackgroundResource(R.drawable.ic_tag_choice_short)
            selectedButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))

            // 버튼 집합에서 선택된 버튼 제거
            selectedLanguageButton.remove(selectedButton)

            if (currentLanguage == 1) {
                selectedLanguageKName.remove(buttonText)
            } else {
                selectedLanguageKName.remove(buttonText)
                selectedLanguageFName.remove(originalText)
            }
        } else {
            // 선택된 버튼 상태 업데이트
            selectedButton.setBackgroundResource(R.drawable.ic_tag_choice_short_selected)
            selectedButton.setTextColor(resources.getColor(R.color.white, null))

            // 선택된 버튼 집합에 현재 버튼 추가
            selectedLanguageButton.add(selectedButton)

            if (currentLanguage == 1) {
                selectedLanguageKName.add(buttonText.toString())
            } else {
                selectedLanguageKName.add(buttonText.toString())
                selectedLanguageFName.add(originalText.toString())
            }
        }
    }

    // 언어 초기화
    private fun resetLanguageSelection() {
        // 버튼 컨테이너에서 모든 버튼을 가져옴
        val buttonContainer = mBinding?.languageButtonsContainer
        val buttons = buttonContainer?.children?.filterIsInstance<Button>() ?: return

        // 모든 버튼의 배경과 텍스트 색상 초기화
        buttons.forEach { button ->
            button.setBackgroundResource(R.drawable.ic_tag_choice_short)
            button.setTextColor(resources.getColor(R.color.filter_tag_text, null))
        }

        selectedLanguageButton = mutableSetOf()
        selectedLanguageKName = mutableSetOf()
        selectedLanguageFName = mutableSetOf()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}