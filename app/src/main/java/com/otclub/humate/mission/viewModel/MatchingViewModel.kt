package com.otclub.humate.mission.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.MatchingResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchingViewModel : ViewModel() {
    private val missionService: MissionService =
        RetrofitConnection.getInstance().create(MissionService::class.java)
    val matchingResponseDTOList = MutableLiveData<List<MatchingResponseDTO>>()

    fun fetchMatching() {
        missionService.getMatchings().enqueue(object : Callback<List<MatchingResponseDTO>> {
            override fun onResponse(
                call: Call<List<MatchingResponseDTO>>,
                response: Response<List<MatchingResponseDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    matchingResponseDTOList.value = response.body()
                    Log.i("응답 데이터 : ", response.body().toString())
                } else {
                    Log.e("응답 오류", "응답이 실패하였습니다. 상태 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MatchingResponseDTO>>, t: Throwable) {
                Log.e("네트워크 오류", "요청 실패: ${t.message}", t)
            }
        })
    }
}
