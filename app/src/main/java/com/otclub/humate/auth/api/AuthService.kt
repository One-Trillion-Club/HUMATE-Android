package com.otclub.humate.auth.api

import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.auth.data.GeneratePhoneCodeRequestDTO
import com.otclub.humate.auth.data.LogInRequestDTO
import com.otclub.humate.auth.data.LogInResponseDTO
import com.otclub.humate.auth.data.SendPhoneCodeRequestDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    fun logIn(@Body logInRequestDTO: LogInRequestDTO): Call<CommonResponseDTO>

    // 휴대폰 인증 번호 생성 요청
    @POST("auth/phone/code")
    fun generatePhoneCode(@Body generatePhoneCodeRequestDTO: GeneratePhoneCodeRequestDTO): Call<CommonResponseDTO>

    // 휴대폰 인증 요청
    @POST("auth/phone/verification")
    fun sendPhoneCode(@Body sendPhoneCodeRequestDTO: SendPhoneCodeRequestDTO): Call<CommonResponseDTO>
}