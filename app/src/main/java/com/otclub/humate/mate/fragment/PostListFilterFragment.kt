package com.otclub.humate.mate.fragment

import android.app.DatePickerDialog
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otclub.humate.BuildConfig.GENDER
import com.otclub.humate.BuildConfig.TEST_MEMBER
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostListFilterBinding
import com.otclub.humate.mate.data.LocalizedBranch
import com.otclub.humate.mate.viewmodel.PostViewModel
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * 매칭글 전체 조회 시 필터링 적용 Adapter
 * @author 김지현
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	김지현        최초 생성
 * 2024.08.03   김지현        전체적인 UI 수정
 * 2024.08.05   김지현        상단바 수정
 * 2024.08.07   김지현        영어 버전 추가
 * </pre>
 */
class PostListFilterFragment : Fragment() {

    private var mBinding : MateFragmentPostListFilterBinding? = null
    private val binding get() = mBinding!!

    private lateinit var postViewModel: PostViewModel

    private var selectedGenderButton: Button? = null
    private var selectedLanguageButton = mutableSetOf<Button>()

    private var selectedDate: String? = null
    private val selectedBranchesId = mutableSetOf<Int>()
    private val selectedBranchesName = mutableSetOf<String>()
    private var selectedGender: String? = null
    private var selectedLanguage = mutableSetOf<String>()

    private var filters = mutableMapOf(
        "gender" to "m",
        "memberId" to "K_1"
        // 초기 필터 값 설정
    )

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostListFilterBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

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

        mBinding = binding

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentLanguage = sharedPreferencesManager.getLanguage()

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

        // 툴바 사용
        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(R.string.filter)

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
                        selectedBranchesName.add(name)
                    }
                }

                val keyword = postViewModel.filterData?.keyword
                val tagName = postViewModel.filterData?.tagName
                postViewModel.updateFilterData(
                    matchDate = selectedDate.takeIf { !it.isNullOrBlank()},
                    matchBranch = selectedBranchesName,
                    matchGender = selectedGender,
                    matchLanguage = selectedLanguage,
                    keyword = keyword.takeIf { !it.isNullOrBlank() },
                    tagName = tagName.takeIf { !it.isNullOrBlank() }
                )
                findNavController().navigate(R.id.action_postListFilterFragment_to_postListFragment)
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

        // 필터 정보에 따라 버튼 상태 초기화
        initializeFilters()

    }

    /**
     * 이름으로 매칭 지점 ID 조회
     */
    private fun getBranchIdByName(branchName: String, currentLanguage: Int): Int? {
        return LocalizedBranch.values().firstOrNull {
            when (currentLanguage) {
                1 -> it.koreanName == branchName // 한국어 설정
                else -> it.englishName == branchName // 영어 설정
            }
        }?.id
    }

    /**
     * 필터링 초기화
     */
    private fun initializeFilters() {
        // 날짜 초기화
        filters["matchDate"]?.let { matchDate ->
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
        filters["matchBranch"]?.let { matchBranch ->
            val currentLanguage = sharedPreferencesManager.getLanguage()
            val branchId = getBranchIdByName(matchBranch, currentLanguage)
            selectedBranchesId.clear()
            matchBranch.split(", ").forEach { branch ->
                if (branchId != null) {
                    selectedBranchesId.add(branchId)
                }
                // 선택된 버튼 스타일 적용
                setButtonSelectedState(branch, true)
            }
        }

        // 성별 초기화
        filters["matchGender"]?.let { matchGender ->
            when (matchGender) {
                "m" -> updateGenderButtonStates(mBinding?.maleButton ?: return, "m")
                "f" -> updateGenderButtonStates(mBinding?.femaleButton ?: return, "f")
            }
        }

        // 언어 초기화
        filters["matchLanguage"]?.let { matchLanguage ->
            selectedLanguage.clear()
            matchLanguage.split(", ").forEach { language ->
                selectedLanguage.add(language)
                // 선택된 버튼 스타일 적용
                setLanguageButtonSelectedState(language, true)
            }
        }

        // keyword와 tagName을 필터 데이터에 설정
        postViewModel.filterData?.keyword?.takeIf { !it.isNullOrBlank() }?.let { keyword ->
            // keyword를 필터에 적용
        }
        postViewModel.filterData?.tagName?.takeIf { !it.isNullOrBlank() }?.let { tagName ->
            // tagName을 필터에 적용
        }
    }

    /**
     * 매칭 지점 버튼 선택 시
     */
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

    /**
     * 매칭 언어 버튼 선택 시
     */
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

    /**
     * 매칭 날짜 선택하기
     */
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

    /**
     * 매칭 날짜 초기화하기
     */
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

    /**
     * 매칭 지점 선택하기
     */
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

    /**
     * 매칭 지점 초기화하기
     */
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

    /**
     * 매칭 성별 선택하기
     */
    private fun setupGenderButtons() {
        val maleButton: Button = mBinding?.maleButton ?: return
        val femaleButton: Button = mBinding?.femaleButton ?: return

        maleButton.setOnClickListener {
            updateGenderButtonStates(maleButton, "m")
        }

        femaleButton.setOnClickListener {
            updateGenderButtonStates(femaleButton, "f")
        }
    }

    /**
     * 매칭 성별 버튼 상태 업데이트하기
     */
    private fun updateGenderButtonStates(selectedButton: Button, gender: String) {
        val maleButton: Button = mBinding?.maleButton ?: return
        val femaleButton: Button = mBinding?.femaleButton ?: return

        // 현재 선택된 버튼과 다른 버튼을 업데이트
        val unselectedButton = if (selectedButton == maleButton) femaleButton else maleButton

        selectedButton.setBackgroundResource(R.drawable.ic_tag_choice_short_selected)
        selectedButton.setTextColor(resources.getColor(R.color.white, null))

        unselectedButton.setBackgroundResource(R.drawable.ic_tag_choice_short)
        unselectedButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))

        // 선택된 버튼 업데이트
        selectedGenderButton = selectedButton

        // 성별 저장
        selectedGender = gender
    }

    /**
     * 매칭 성별 초기화하기
     */
    private fun resetGenderSelection() {
        val maleButton: Button = mBinding?.maleButton ?: return
        val femaleButton: Button = mBinding?.femaleButton ?: return

        // 모든 버튼의 스타일 초기화
        maleButton.setBackgroundResource(R.drawable.ic_tag_choice_short)
        maleButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))

        femaleButton.setBackgroundResource(R.drawable.ic_tag_choice_short)
        femaleButton.setTextColor(resources.getColor(R.color.filter_tag_text, null))

        // 선택된 버튼 초기화
        selectedGenderButton = null
        selectedGender = null
    }

    /**
     * 매칭 언어 선택하기
     */
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

    /**
     * 매칭 성별 초기화하기
     */
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

    /**
     * 매칭 언어 초기화하기
     */
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