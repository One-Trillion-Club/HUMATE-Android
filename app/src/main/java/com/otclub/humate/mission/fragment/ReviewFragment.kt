package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentReviewBinding
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.data.ReviewRequestDTO
import com.otclub.humate.mission.data.ReviewResponseDTO
import com.otclub.humate.mission.viewModel.MissionViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewFragment : Fragment() {
    private val missionViewModel: MissionViewModel by activityViewModels()
    private var mBinding: FragmentReviewBinding? = null
    private var selectedButton: ImageButton? = null
    private var selectedScore: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentReviewBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        missionViewModel.lastCompanionId?.let { fetchReviewData(it) }

        mBinding?.btnExcellent?.setOnClickListener {
            handleButtonSelection(mBinding?.btnExcellent, 3)
        }

        mBinding?.btnGood?.setOnClickListener {
            handleButtonSelection(mBinding?.btnGood, 2)
        }

        mBinding?.btnBad?.setOnClickListener {
            handleButtonSelection(mBinding?.btnBad, 1)
        }

        mBinding?.submitButton?.setOnClickListener {
            submitReview()
        }
    }

    private fun handleButtonSelection(selected: ImageButton?, score: Int) {
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

    private fun resetButtonStates() {
        // Reset all buttons to their default drawable
        mBinding?.btnExcellent?.setImageDrawable(resources.getDrawable(R.drawable.ic_excellent_status, null))
        mBinding?.btnGood?.setImageDrawable(resources.getDrawable(R.drawable.ic_good_status, null))
        mBinding?.btnBad?.setImageDrawable(resources.getDrawable(R.drawable.ic_bad_status, null))
    }

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
                            mateNickname.text = review.mateNickname + "님과의 매칭은 어떠셨나요?"
                        }
                    }
                } else {
                    // Handle the error
                }
            }

            override fun onFailure(call: Call<ReviewResponseDTO>, t: Throwable) {
                // Handle the failure
            }
        })
    }

    private fun submitReview() {
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
                if (response.isSuccessful) {
                    findNavController().navigate(R.id.action_reviewFragment_to_missionFragment)
                    Toast.makeText(
                        requireContext(),
                        "후기 작성에 성공했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    findNavController().navigate(R.id.action_reviewFragment_to_missionFragment)
                    Toast.makeText(
                        requireContext(),
                        "후기 작성에 실패했습니다.",
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
    }
}