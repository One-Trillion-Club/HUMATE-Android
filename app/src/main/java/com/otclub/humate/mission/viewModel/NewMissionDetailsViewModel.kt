package com.otclub.humate.mission.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewMissionDetailsViewModel : ViewModel() {
    private val missionService: MissionService = RetrofitConnection.getInstance().create(MissionService::class.java)
    val newMissionDetailsDTO = MutableLiveData<NewMissionDetailsDTO>()

    fun fetchDetail(activityId: Int) {
        missionService.getNewMissionDetails(activityId).enqueue(object: Callback<NewMissionDetailsDTO> {
            override fun onResponse(
                call: Call<NewMissionDetailsDTO>,
                response: Response<NewMissionDetailsDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    newMissionDetailsDTO.value = response.body()
                    Log.i("newMissionDetailsDTO : ", newMissionDetailsDTO.toString())
                }
            }

            override fun onFailure(call: Call<NewMissionDetailsDTO>, t: Throwable) {
                Log.i("새로운 미션 상세페이지 요청 실패", t.toString())
            }

        } )
    }



}