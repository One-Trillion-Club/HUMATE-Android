package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentReviewBinding
import com.otclub.humate.mission.api.MissionService
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
            handleButtonSelection(mBinding?.btnExcellent)
        }

        mBinding?.btnGood?.setOnClickListener {
            handleButtonSelection(mBinding?.btnGood)
        }

        mBinding?.btnBad?.setOnClickListener {
            handleButtonSelection(mBinding?.btnBad)
        }

        mBinding?.submitButton?.setOnClickListener {
            // Handle submit button click
            // Example: Submit review data
            // submitReview()
        }
    }

    private fun handleButtonSelection(selected: ImageButton?) {
        resetButtonStates()

        selected?.let {
            when (it.id) {
                R.id.btn_excellent -> it.setImageDrawable(resources.getDrawable(R.drawable.ic_excellent_status_selected, null))
                R.id.btn_good -> it.setImageDrawable(resources.getDrawable(R.drawable.ic_good_status_selected, null))
                R.id.btn_bad -> it.setImageDrawable(resources.getDrawable(R.drawable.ic_bad_status_selected, null))
            }
            selectedButton = it
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}