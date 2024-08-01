package com.otclub.humate.mission.api

import com.otclub.humate.mission.data.MissionResponseDTO
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MissionService {
    @GET("activities")
    fun getMissions(@Query("companionId") companionId: String): Call<MissionResponseDTO>

    @GET("activities/{activityId}")
    fun getNewMissionDetails(@Path("activityId") activityId: Int): Call<NewMissionDetailsDTO>

}