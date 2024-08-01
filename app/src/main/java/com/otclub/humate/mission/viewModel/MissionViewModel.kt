package com.otclub.humate.mission.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.MissionResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissionViewModel : ViewModel() {
    val missionResponseDTO = MutableLiveData<MissionResponseDTO>()

    fun fetchMission(companionId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val missionService = retrofit.create(MissionService::class.java)
        val call = missionService.getMissions(companionId)

        call.enqueue(object : Callback<MissionResponseDTO> {
            override fun onResponse(call: Call<MissionResponseDTO>, response: Response<MissionResponseDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("missonResponse : ", response.body().toString())
                    missionResponseDTO.value = response.body()
                }
            }

            override fun onFailure(call: Call<MissionResponseDTO>, t: Throwable) {
                // 오류 처리
            }
        })
    }


}