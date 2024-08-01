package com.otclub.humate

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.otclub.humate.databinding.FragmentPostDetailBinding


class PostDetailFragment : Fragment() {

    private var mBinding : FragmentPostDetailBinding? = null
    private val selectedButtons = mutableSetOf<Button>() // 현재 선택된 버튼들을 추적

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? MainActivity
        activity?.let {
            val toolbar = it.getToolbar() // MainActivity의 Toolbar를 가져옴
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)

            // 버튼의 가시성 설정
            val showLeftButton = false
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 액션 바의 타이틀을 설정하거나 액션 바의 다른 속성을 조정
            it.setToolbarTitle("메이트 찾기")
        }

        // buttonContainer 레이아웃을 가져오기
        val buttonContainer = view.findViewById<LinearLayout>(R.id.button_container)

        // 동적으로 버튼 추가
        val buttonsData = listOf("의류", "뷰티", "악세서리", "신발류", "한식", "일식", "양식", "중식", "분식", "팝업스토어", "전시", "공연") // 서버나 데이터베이스에서 가져온 데이터


        for (buttonText in buttonsData) {
            val button = Button(ContextThemeWrapper(requireContext(), R.style.TagButtonUnselected), null, R.style.TagButtonUnselected)
            button.text = buttonText
            val params = LinearLayout.LayoutParams(
                170,
                70
            )
            // 버튼 간의 간격을 설정 (예: 8dp)
            params.setMargins(8, 0, 8, 0)
            button.layoutParams = params
            button.gravity = Gravity.CENTER
            button.setPadding(16, 8, 16, 8)
            button.setBackgroundResource(R.drawable.tag_button_unselected)
            button.setTextColor(resources.getColor(R.color.dark_gray, null))

            // 버튼 클릭 이벤트 설정
            button.setOnClickListener {
                handleButtonClick(button)
            }

            buttonContainer.addView(button)
        }

    }

    private fun handleButtonClick(clickedButton: Button) {
        if (selectedButtons.contains(clickedButton)) {
            // 선택 해제
            clickedButton.setBackgroundResource(R.drawable.tag_button_unselected)
            clickedButton.setTextColor(resources.getColor(R.color.dark_gray, null))
            selectedButtons.remove(clickedButton)
        } else {
            // 선택
            clickedButton.setBackgroundResource(R.drawable.tag_button_selected)
            clickedButton.setTextColor(resources.getColor(R.color.white, null)) // 선택된 버튼의 글자색을 흰색으로 변경
            selectedButtons.add(clickedButton)
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}