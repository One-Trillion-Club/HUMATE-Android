package com.otclub.humate.mate.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostWriteBinding
import com.otclub.humate.mate.data.Tag
import com.otclub.humate.mate.viewmodel.PostWriteViewModel

class PostWriteFragment : Fragment()  {

    private var mBinding : MateFragmentPostWriteBinding? = null
    private val binding get() = mBinding!!

    private lateinit var postWriteViewModel: PostWriteViewModel

    private lateinit var storeItemsContainer: LinearLayout
    private lateinit var addStoreButton: Button

    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostWriteBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postWriteViewModel = ViewModelProvider(requireActivity()).get(PostWriteViewModel::class.java)

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
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 액션 바의 타이틀을 설정하거나 액션 바의 다른 속성을 조정
            it.setToolbarTitle("매칭글 쓰기")
        }

        // 태그 데이터 설정
        val tags = listOf(
            Tag(
                iconResId = R.drawable.mate_shopping,
                title = "쇼핑",
                buttons = listOf("의류", "뷰티", "악세서리", "신발류")
            ),
            Tag(
                iconResId = R.drawable.mate_food,
                title = "식사",
                buttons = listOf("한식", "일식", "양식", "중식", "분식")
            ),
            Tag(
                iconResId = R.drawable.mate_event,
                title = "행사",
                buttons = listOf("팝업스토어", "전시", "공연")
            )
        )

        tags.forEach { tag ->
            addTagToLayout(tag)
        }

        storeItemsContainer = binding.storeItemsContainer
        addStoreButton = binding.addStoreButton

        addStoreButton.setOnClickListener {
            addStoreItem()
        }

    }

    private fun addTagToLayout(tag: Tag) {
        val container = binding.tagContainer

        // 수평으로 아이콘, 제목, 버튼들을 배치할 LinearLayout 생성
        val categoryLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
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
            text = tag.title
            textSize = 12f
            setTextColor(resources.getColor(R.color.black, null))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                setMargins(0, 0, 8.dpToPx(), 0)
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

        for (buttonText in tag.buttons) {
            val button = Button(ContextThemeWrapper(requireContext(), R.style.TagButtonUnselected), null, R.style.TagButtonUnselected).apply {
                text = buttonText
                val params = LinearLayout.LayoutParams(
                    180,
                    70
                ).apply {
                    setMargins(8, 0, 8,0)
                }
                layoutParams = params
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                setBackgroundResource(R.drawable.tag_button_unselected)
                setTextColor(resources.getColor(R.color.dark_gray, null))
            }

            buttonContainer.addView(button)
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

    private fun addStoreItem() {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.mate_item_store, storeItemsContainer, false)

        val spinner: Spinner = itemView.findViewById(R.id.select_spinner)
        val contentInput: EditText = itemView.findViewById(R.id.content_input)
        val deleteButton: ImageButton = itemView.findViewById(R.id.place_delete_button)

        // Spinner의 항목 설정
        val options = arrayOf("매장", "팝업스토어")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(R.layout.mate_spinner_item)
        spinner.adapter = adapter

        // 삭제 버튼 클릭 리스너 설정
        deleteButton.setOnClickListener {
            storeItemsContainer.removeView(itemView)
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

}