package com.otclub.humate.mission.api

import com.otclub.humate.mission.data.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface MissionService {
    @GET("activities")
    fun getMissions(@Query("companionId") companionId: Int): Call<MissionResponseDTO>

    @GET("activities/{activityId}")
    fun getNewMissionDetails(@Path("activityId") activityId: Int): Call<NewMissionDetailsDTO>

    @GET("activities/histories/{companionActivityId}")
    fun getClearedMissionDetails(@Path("companionActivityId") companionActivityId: Int): Call<ClearedMissionDetailsDTO>

    @GET("companions")
    fun getMatchings(): Call<List<MatchingResponseDTO>>

    @Multipart
    @POST("activities/upload")
    fun uploadActivity(
        @Part uploadActivityRequestDTO: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    ): Call<CommonResponseDTO>

    @DELETE("companions/finish")
    fun finishCompanion(@Query("companionId") companionId: Int): Call<CommonResponseDTO>

    @GET("reviews")
    fun getReview(@Query("companionId") companionId: Int): Call<ReviewResponseDTO>

}