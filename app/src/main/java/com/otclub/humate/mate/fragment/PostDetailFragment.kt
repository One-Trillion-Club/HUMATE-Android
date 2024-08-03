package com.otclub.humate.mate.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.MateFragmentPostDetailBinding
import com.otclub.humate.mate.adapter.PostDetailAdapter
import com.otclub.humate.mate.adapter.PostListAdapter
import com.otclub.humate.mate.data.PostDetailResponseDTO
import com.otclub.humate.mate.viewmodel.PostDetailViewModel

class PostDetailFragment : Fragment() {

    private var mBinding : MateFragmentPostDetailBinding? = null
    private val binding get() = mBinding!!
    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var postDetailViewModel: PostDetailViewModel
    private lateinit var postDetailAdapter: PostDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = MateFragmentPostDetailBinding.inflate(inflater, container, false)

        // ViewModel 초기화
        postDetailViewModel = ViewModelProvider(requireActivity()).get(PostDetailViewModel::class.java)

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
            it.setToolbarTitle("매칭글 정보")
        }

        val postId = args.postId

        // 상세 게시글 정보 요청
        postDetailViewModel.getPostDetail(postId, onSuccess = { postDetail -> updateUI(postDetail)
        }, onError = { errorMessage ->
            // 에러 처리
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun updateUI(postDetail: PostDetailResponseDTO) {
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
        val matchDateText = postDetail.matchDate ?: "상관 없음"
        val matchBranchText = postDetail.matchBranch ?: "상관 없음"
        val matchLanguageText = postDetail.matchLanguage ?: "상관 없음"
        val matchGenderText = when (postDetail.matchGender) {
            1 -> "나와 같은 성별"
            2 -> "상관 없음"
            else -> "상관 없음"
        }
        binding.card1.findViewById<TextView>(R.id.card1_text).text = matchDateText
        binding.card2.findViewById<TextView>(R.id.card2_text).text = matchBranchText
        binding.card3.findViewById<TextView>(R.id.card3_text).text = matchGenderText
        binding.card4.findViewById<TextView>(R.id.card4_text).text = matchLanguageText

        // 매장 및 팝업스토어 업데이트
        val placeContainerLayout: LinearLayout = binding.placeContainerLayout
        placeContainerLayout.removeAllViews() // 기존 뷰 제거

        postDetail.postPlaces.forEach { place ->
            val placeView = LayoutInflater.from(context).inflate(R.layout.mate_item_place, placeContainerLayout, false)

            val typeText = when (place.type) {
                1 -> "매장"
                2 -> "팝업스토어"
                else -> "알 수 없음"
            }
            val nameText = place.name ?: "알 수 없음"

            val placeType: TextView = placeView.findViewById<TextView>(R.id.place_type)
            val placeName: TextView = placeView.findViewById<TextView>(R.id.place_name)

            placeType.text = typeText
            placeName.text = nameText

            placeContainerLayout.addView(placeView)
        }

        // 태그 업데이트
// 태그 업데이트
        val tagContainerLayout: LinearLayout = binding.tagContainerLayout
        tagContainerLayout.removeAllViews() // 기존 태그 제거

        postDetail.postTags.forEach { tag ->
            val tagView = TextView(context).apply {
                text = tag.name
                textSize = 8f
                setPadding(30, 8, 30, 8)
                setBackgroundResource(R.drawable.mate_post_detail_tag_background)
                setTextColor(resources.getColor(R.color.humate_main, null)) // 텍스트 색상 설정
                typeface = Typeface.DEFAULT_BOLD // 텍스트를 볼드체로 설정
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 태그 간의 간격을 조정
                    (this as? LinearLayout.LayoutParams)?.apply {
                        marginEnd = 16   // 태그 간의 끝 여백
                    }
                }
            }

            // LinearLayout에 추가
            tagContainerLayout.addView(tagView)
        }

    }
}