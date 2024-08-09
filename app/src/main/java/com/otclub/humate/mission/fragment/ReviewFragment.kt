package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.ReviewFragmentBinding
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.data.ReviewRequestDTO
import com.otclub.humate.mission.data.ReviewResponseDTO
import com.otclub.humate.mission.data.ReviewScore
import com.otclub.humate.mission.viewModel.MissionViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 후기 등록 Fragment
 * @author 손승완
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04 	손승완        최초 생성
 * 2024.08.07 	손승완        후기 등록 예외 처리
 * </pre>
 */
class ReviewFragment : Fragment() {
    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding: ReviewFragmentBinding? = null
    private var selectedButton: ImageButton? = null
    private var selectedScore: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ReviewFragmentBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideBottomNavigationBar()
        val toolbar = mBinding?.toolbar?.toolbar

        toolbar?.let {
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)
            val title: TextView = toolbar.findViewById(R.id.toolbar_title)
            title.setText(getString(R.string.review_write))

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE
        }

        missionViewModel.lastCompanionId?.let { fetchReviewData(it) }

        mBinding?.toolbar?.leftButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding?.btnExcellent?.setOnClickListener {
            handleButtonSelection(mBinding?.btnExcellent, ReviewScore.EXCELLENT.weight)
        }

        mBinding?.btnGood?.setOnClickListener {
            handleButtonSelection(mBinding?.btnGood, ReviewScore.GOOD.weight)
        }

        mBinding?.btnBad?.setOnClickListener {
            handleButtonSelection(mBinding?.btnBad, ReviewScore.BAD.weight)
        }

        mBinding?.submitButton?.setOnClickListener {
            submitReview()
        }


    }

    /**
     * 후기 점수 버튼 클릭에 따른 처리
     */
    private fun handleButtonSelection(selected: ImageButton?, score: Double) {
        resetButtonStates()

        selected?.let {
            when (it.id) {
                R.id.btn_excellent -> it.setImageDrawable(resources.getDrawable(R.drawable.ic_excellent_status_selected, null))
                R.id.btn_good -> it.setImageDrawable(resources.getDrawable(R.drawable.ic_good_status_selected, null))
                R.id.btn_bad -> it.setImageDrawable(resources.getDrawable(R.drawable.ic_bad_status_selected, null))
            }
            selectedButton = it
            selectedScore = score
        }
        selectedButton = selected
    }

    /**
     * 후기 점수 버튼 클릭 초기화
     */
    private fun resetButtonStates() {
        // Reset all buttons to their default drawable
        mBinding?.btnExcellent?.setImageDrawable(resources.getDrawable(R.drawable.ic_excellent_status, null))
        mBinding?.btnGood?.setImageDrawable(resources.getDrawable(R.drawable.ic_good_status, null))
        mBinding?.btnBad?.setImageDrawable(resources.getDrawable(R.drawable.ic_bad_status, null))
    }

    /**
     * 후기 등록 페이지 데이터 조회
     */
    private fun fetchReviewData(companionId: Int) {
        val reviewService = RetrofitConnection.getInstance().create(MissionService::class.java)
        reviewService.getReview(companionId).enqueue(object : Callback<ReviewResponseDTO> {
            override fun onResponse(
                call: Call<ReviewResponseDTO>,
                response: Response<ReviewResponseDTO>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { review ->
                        // Bind data to UI elements
                        mBinding?.apply {
                            postTitle.text = review.postTitle
                            matchBranch.text = review.matchBranch
                            matchDate.text = review.matchDate
                            mateNickname.text = getString(R.string.matching_nickname, review.mateNickname)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.review_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ReviewResponseDTO>, t: Throwable) {
                // Handle the failure
            }
        })
    }

    /**
     * 후기 등록
     */
    private fun submitReview() {
        if (selectedScore == 0.0) {
            Toast.makeText(requireContext(), getString(R.string.review_score_failed), Toast.LENGTH_SHORT).show()
            return
        }

        val content = mBinding?.inputBox?.text.toString()
        val companionId = missionViewModel.lastCompanionId ?: return

        val reviewRequest = ReviewRequestDTO(
            companionId = companionId,
            content = content,
            score = selectedScore
        )

        val reviewService = RetrofitConnection.getInstance().create(MissionService::class.java)
        reviewService.submitReview(reviewRequest).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: Response<CommonResponseDTO>
            ) {
                if (response.body()?.success == true) {
                    findNavController().navigate(R.id.action_reviewFragment_to_missionFragment)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.review_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    findNavController().navigate(R.id.action_reviewFragment_to_missionFragment)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.review_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {

            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNavigationBar()
    }
}