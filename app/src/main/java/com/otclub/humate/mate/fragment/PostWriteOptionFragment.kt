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
import com.otclub.humate.mate.viewmodel.PostWriteViewModel
import java.text.SimpleDateFormat
import java.util.*

class PostWriteOptionFragment : Fragment() {

    private var mBinding : MateFragmentPostWriteOptionBinding? = null
    private val binding get() = mBinding!!

    private lateinit var postWriteViewModel: PostWriteViewModel

    private var options = mutableMapOf<String, String>()
    private var selectedDate: String? = null
    private val selectedBranches = mutableSetOf<String>()
    private var selectedGender: Int? = null
    private var selectedLanguage = mutableSetOf<String>()

    private var selectedGenderButton: CardView? = null
    private var selectedLanguageButton = mutableSetOf<Button>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostWriteOptionBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postWriteViewModel = ViewModelProvider(requireActivity()).get(PostWriteViewModel::class.java)

        mBinding = binding

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            title.setText("매칭 정보 선택")

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 확인 버튼 이벤트 처리
            rightButton.setOnClickListener {
                postWriteViewModel.updateOptionData(
                    matchDate = selectedDate.takeIf { !it.isNullOrBlank()},
                    matchBranch = selectedBranches,
                    matchGender = selectedGender,
                    matchLanguage = selectedLanguage
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

        // 동적으로 버튼 추가
        val buttonsData = listOf("더현대 서울", "더현대 대구", "압구정본점", "무역센터점", "천호점", "신촌점", "미아점",
            "목동점", "중동점", "킨텍스점", "디큐브시티", "판교점", "부산점", "울산점", "울산동구점", "충청점") // 서버나 데이터베이스에서 가져온 데이터


        for (buttonText in buttonsData) {
            val button = Button(ContextThemeWrapper(requireContext(), R.style.MatchBranchFilterUnselected), null, R.style.MatchBranchFilterUnselected)
            button.text = buttonText

            // 텍스트의 실제 너비 측정
            val paint = Paint()
            paint.textSize = button.textSize
            val textWidth = paint.measureText(buttonText)

            // 여유 공간(패딩)을 포함한 버튼 너비 계산
            val padding = 100 // 좌우 패딩 (예: 32dp * 2)
            val buttonWidth = textWidth.toInt() + padding

            val params = LinearLayout.LayoutParams(
                buttonWidth,
                90
            )

            // 버튼 간의 간격을 설정
            params.setMargins(12, 0, 12, 0)
            button.layoutParams = params
            button.gravity = Gravity.CENTER
            button.setPadding(16, 8, 16, 8)
            button.setBackgroundResource(R.drawable.ic_tag_choice_long)
            button.setTextColor(resources.getColor(R.color.filter_tag_text, null))

            // 버튼 클릭 이벤트 설정
            button.setOnClickListener {
                handleButtonClick(button)
            }

            buttonContainer.addView(button)
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
            selectedBranches.clear()
            matchBranch.split(", ").forEach { branch ->
                selectedBranches.add(branch)
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
            selectedLanguage.clear()
            matchLanguage.split(", ").forEach { language ->
                selectedLanguage.add(language)
                // 선택된 버튼 스타일 적용
                setLanguageButtonSelectedState(language, true)
            }
        }
    }

    private fun setButtonSelectedState(branch: String, isSelected: Boolean) {
        // 버튼 컨테이너에서 해당 지점 버튼을 찾아 상태를 설정
        val buttonContainer = mBinding?.branchSelectionsContainer
        buttonContainer?.children?.filterIsInstance<Button>()?.forEach { button ->
            if (button.text.toString() == branch) {
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
            "한국어" -> mBinding?.koreanButton
            "영어" -> mBinding?.englishButton
            "일본어" -> mBinding?.japaneseButton
            "중국어" -> mBinding?.chineseButton
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

    // 지점 선택
    private fun handleButtonClick(clickedButton: Button) {
        // 버튼 스타일 변경
        if (selectedBranches.contains(clickedButton.text.toString())) {
            // 이미 선택된 버튼이면 선택 해제
            clickedButton.setBackgroundResource(R.drawable.ic_tag_choice_long)
            clickedButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))
            selectedBranches.remove(clickedButton.text.toString())
        } else {
            // 새로 선택된 버튼이면 강조
            clickedButton.setBackgroundResource(R.drawable.ic_tag_choice_long_selected)
            clickedButton.setTextColor(resources.getColor(R.color.white, null))
            selectedBranches.add(clickedButton.text.toString())
        }
    }

    // 지점 초기화
    private fun resetBranchSelection() {
        // 버튼 컨테이너에서 모든 버튼을 가져옴
        val buttonContainer = mBinding?.branchSelectionsContainer
        val buttons = buttonContainer?.children?.filterIsInstance<Button>() ?: return

        // 모든 버튼의 배경과 텍스트 색상 초기화
        buttons.forEach { button ->
            button.setBackgroundResource(R.drawable.ic_tag_choice_long)
            button.setTextColor(resources.getColor(R.color.filter_tag_text, null))
            selectedBranches.remove(button.text.toString())
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

        if (isAlreadySelected) {
            // 선택 상태를 취소
            selectedButton.setBackgroundResource(R.drawable.ic_tag_choice_short)
            selectedButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))

            // 버튼 집합에서 선택된 버튼 제거
            selectedLanguageButton.remove(selectedButton)
            selectedLanguage.remove(language)
        } else {
            // 선택된 버튼 상태 업데이트
            selectedButton.setBackgroundResource(R.drawable.ic_tag_choice_short_selected)
            selectedButton.setTextColor(resources.getColor(R.color.white, null))

            // 선택된 버튼 집합에 현재 버튼 추가
            selectedLanguageButton.add(selectedButton)
            selectedLanguage.add(language)
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
            selectedLanguage.remove(button.text.toString())
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}