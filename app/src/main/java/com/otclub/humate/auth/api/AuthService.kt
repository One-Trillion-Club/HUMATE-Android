package com.otclub.humate.auth.api

import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.auth.data.GeneratePhoneCodeRequestDTO
import com.otclub.humate.auth.data.LoginRequestDTO
import com.otclub.humate.auth.data.SignUpRequestDTO
import com.otclub.humate.auth.data.VerifyPassportRequestDTO
import com.otclub.humate.auth.data.VerifyPhoneCodeRequestDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AuthService {
    @POST("auth/login")
    fun logIn(@Body logInRequestDTO: LoginRequestDTO): Call<CommonResponseDTO>

    // 휴대폰 인증 번호 생성 요청
    @POST("auth/phone/code")
    fun generatePhoneCode(@Body generatePhoneCodeRequestDTO: GeneratePhoneCodeRequestDTO): Call<CommonResponseDTO>

    // 휴대폰 인증
    @POST("auth/phone/verification")
    fun verifyPhoneCode(@Body verifyPhoneCodeRequestDTO: VerifyPhoneCodeRequestDTO): Call<CommonResponseDTO>

    // 아이디 중복 체크
    @GET("auth/check-loginid")
    fun checkLoginId(@Query("loginId") loginId: String): Call<CommonResponseDTO>

    // 닉네임 중복 체크 todo: 멤버서비스로 옮기기
    @GET("member/check-nickname")
    fun checkNickname(@Query("nickname") nickname: String): Call<CommonResponseDTO>

    // 로그아웃
    @POST("auth/logout")
    fun logOut(): Call<CommonResponseDTO>

    // 여권 인증
    @POST("auth/passport/verification")
    fun verifyPassport(@Body verifyPassportRequestDTO: VerifyPassportRequestDTO): Call<CommonResponseDTO>

    // 회원가입
    @Multipart
    @POST("auth/signup")
    fun signUp(
        @Part("signUpRequestDTO") dto: SignUpRequestDTO,
        @Part image: MultipartBody.Part?
    ): Call<CommonResponseDTO>
}