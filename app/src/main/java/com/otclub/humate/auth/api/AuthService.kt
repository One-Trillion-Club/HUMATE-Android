package com.otclub.humate.auth.api

import com.otclub.humate.auth.data.LogInRequestDTO
import com.otclub.humate.auth.data.LogInResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    fun logIn(@Body logInRequestDTO: LogInRequestDTO): Call<LogInResponseDTO>
}