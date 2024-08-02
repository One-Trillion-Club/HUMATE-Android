package com.otclub.humate.mission.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClearedMissionDetailsViewModel : ViewModel() {
    private val missionService: MissionService =
        RetrofitConnection.getInstance().create(MissionService::class.java)

    val clearedMissionDetailsDTO = MutableLiveData<ClearedMissionDetailsDTO>()

    fun fetchDetail(companionActivityId: Int) {
        missionService.getClearedMissionDetails(companionActivityId).enqueue(object :
            Callback<ClearedMissionDetailsDTO> {
            override fun onResponse(
                call: Call<ClearedMissionDetailsDTO>,
                response: Response<ClearedMissionDetailsDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    clearedMissionDetailsDTO.value = response.body()
                    Log.i("응답 데이터 : ", response.body().toString())
                }
            }

            override fun onFailure(call: Call<ClearedMissionDetailsDTO>, t: Throwable) {
                Log.e("완료된 미션 상세 페이지 응답 실패 ", t.toString())
            }
        })
    }

}