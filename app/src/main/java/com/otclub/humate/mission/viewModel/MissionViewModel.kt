package com.otclub.humate.mission.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.data.MatchingResponseDTO
import com.otclub.humate.mission.data.MissionResponseDTO
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissionViewModel : ViewModel() {
    private val missionService: MissionService =
        RetrofitConnection.getInstance().create(MissionService::class.java)
    val missionResponseDTO = MutableLiveData<MissionResponseDTO>()
    val matchingResponseDTOList = MutableLiveData<List<MatchingResponseDTO>>()
    val clearedMissionDetailsDTO = MutableLiveData<ClearedMissionDetailsDTO>()
    val newMissionDetailsDTO = MutableLiveData<NewMissionDetailsDTO>()
    var lastCompanionId: Int? = null
    var lastActivityId: Int? = null

    fun fetchMission(companionId: Int) {
        if (companionId == 0) return
        lastCompanionId = companionId
        Log.i("lastCompanionId : ", lastCompanionId.toString())

        missionService.getMissions(lastCompanionId!!).enqueue(object :
            Callback<MissionResponseDTO> {
            override fun onResponse(
                call: Call<MissionResponseDTO>,
                response: Response<MissionResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("missonResponse : ", response.body().toString())
                    missionResponseDTO.value = response.body()
                }
            }

            override fun onFailure(call: Call<MissionResponseDTO>, t: Throwable) {
                Log.e("미션 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }

    fun fetchClearedMissionDetails(companionActivityId: Int) {
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

    fun fetchNewMissionDetails(activityId: Int) {
        lastActivityId = activityId
        missionService.getNewMissionDetails(activityId)
            .enqueue(object : Callback<NewMissionDetailsDTO> {
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

            })
    }

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

    fun fetchFinishCompanion() {
        lastCompanionId?.let {
            missionService.finishCompanion(it).enqueue(object : Callback<CommonResponseDTO> {
                override fun onResponse(
                    call: Call<CommonResponseDTO>,
                    response: Response<CommonResponseDTO>
                ) {

                }

                override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {

                }

            })
        }
    }

}