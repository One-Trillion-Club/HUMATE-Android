package com.otclub.humate.mission.api

import com.otclub.humate.mission.data.ClearedMissionDetailsDTO
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.mission.data.MatchingResponseDTO
import com.otclub.humate.mission.data.MissionResponseDTO
import com.otclub.humate.mission.data.NewMissionDetailsDTO
import com.otclub.humate.mission.data.ReviewRequestDTO
import com.otclub.humate.mission.data.ReviewResponseDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 활동 Service
 * @author 손승완
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * </pre>
 */
interface MissionService {
    /**
     * 활동(완료된, 새로운) 목록 조회
     */
    @GET("activities")
    fun getMissions(@Query("companionId") companionId: Int): Call<MissionResponseDTO>

    /**
     * 새로운 활동 상세 조회
     */
    @GET("activities/{activityId}")
    fun getNewMissionDetails(@Path("activityId") activityId: Int): Call<NewMissionDetailsDTO>

    /**
     * 완료된 활동 상세 조회
     */
    @GET("activities/histories/{companionActivityId}")
    fun getClearedMissionDetails(@Path("companionActivityId") companionActivityId: Int): Call<ClearedMissionDetailsDTO>

    /**
     * 동행 목록 조회
     */
    @GET("companions")
    fun getMatchings(): Call<List<MatchingResponseDTO>>

    /**
     * 수행한 활동 등록
     */
    @Multipart
    @POST("activities/upload")
    fun uploadActivity(
        @Part uploadActivityRequestDTO: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    ): Call<CommonResponseDTO>

    /**
     * 동행 종료
     */
    @DELETE("companions/finish")
    fun finishCompanion(@Query("companionId") companionId: Int): Call<CommonResponseDTO>

    /**
     * 후기 페이지 조회
     */
    @GET("reviews")
    fun getReview(@Query("companionId") companionId: Int): Call<ReviewResponseDTO>

    /**
     * 후기 작성
     */
    @POST("reviews")
    fun submitReview(@Body reviewRequest: ReviewRequestDTO): Call<CommonResponseDTO>

}