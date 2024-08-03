package com.otclub.humate.mission.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private var selectedButton: Button? = null

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

        mBinding?.submitButton?.setOnClickListener {
            // 후기 남기기 post 요청
        }
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