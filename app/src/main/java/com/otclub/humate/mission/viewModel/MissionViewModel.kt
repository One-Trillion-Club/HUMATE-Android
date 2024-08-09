package com.otclub.humate.mission.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.otclub.humate.mission.api.MissionService
import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.data.MatchingResponseDTO
import com.otclub.humate.mission.data.MissionResponseDTO
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.retrofit.RetrofitConnection
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 활동 관련 ViewModel
 * @author 손승완
 * @since 2024.08.01
 * @version 1.1
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.03 	손승완        활동 관련 변수 추가, 활동 상세, 동행 목록 조회 기능 추가
 * </pre>
 */
class MissionViewModel(application: Application) : AndroidViewModel(application) {
    private val missionService: MissionService =
        RetrofitConnection.getInstance().create(MissionService::class.java)
    val sharedPreferencesManager: SharedPreferencesManager =
        SharedPreferencesManager(application)
    val missionResponseDTO = MutableLiveData<MissionResponseDTO>()
    val matchingResponseDTOList = MutableLiveData<List<MatchingResponseDTO>>()
    val clearedMissionDetailsDTO = MutableLiveData<ClearedMissionDetailsDTO>()
    val newMissionDetailsDTO = MutableLiveData<NewMissionDetailsDTO>()
    var lastCompanionId: Int? = null
    var lastActivityId: Int? = null
    var isFinished: Int? = null

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
                    isFinished = missionResponseDTO.value?.isFinished
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

}