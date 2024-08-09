package com.otclub.humate.member.api

import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.mate.data.MateDetailResponseDTO
import com.otclub.humate.member.data.ModifyProfileRequestDTO
import com.otclub.humate.member.data.ProfileResponseDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 회원 Service
 * @author 조영욱
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03  	조영욱        최초 생성
 * 2024.08.04  	조영욱        내 프로필 조회/수정 추가
 * 2024.08.05  	조영욱        상대방 프로필 조회 추가
 * </pre>
 */
interface MemberService {
    /**
     * 닉네임 중복 체크
     */
    @GET("members/check-nickname")
    fun checkNickname(@Query("nickname") nickname: String): Call<CommonResponseDTO>

    /**
     * 내 프로필 조회
     */
    @GET("members/profile")
    fun getMyProfile(): Call<ProfileResponseDTO>

    /**
     * 내 프로필 수정
     */
    @Multipart
    @PUT("members/profile")
    fun modifyProfile(
        @Part("modifyProfileRequestDTO") dto: ModifyProfileRequestDTO,
        @Part image: MultipartBody.Part?
    ): Call<CommonResponseDTO>

    /**
     * 내 메이트 조회
     */
    @GET("members/my-mates")
    fun getMyMateList(): Call<List<MateDetailResponseDTO>>

    /**
     * 상대방 프로필 조회
     */
    @GET("members/{memberId}")
    fun getOtherMemberProfile(@Path("memberId") memberId: String): Call<ProfileResponseDTO>
}