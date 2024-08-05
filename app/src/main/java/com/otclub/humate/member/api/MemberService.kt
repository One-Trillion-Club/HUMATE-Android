package com.otclub.humate.member.api

import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.auth.data.GeneratePhoneCodeRequestDTO
import com.otclub.humate.auth.data.LoginRequestDTO
import com.otclub.humate.auth.data.SignUpRequestDTO
import com.otclub.humate.auth.data.VerifyPassportRequestDTO
import com.otclub.humate.auth.data.VerifyPhoneCodeRequestDTO
import com.otclub.humate.member.data.ProfileResponseDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MemberService {
    // 닉네임 중복 체크
    @GET("members/check-nickname")
    fun checkNickname(@Query("nickname") nickname: String): Call<CommonResponseDTO>

    @GET("members/profile")
    fun getMyProfile(): Call<ProfileResponseDTO>
}