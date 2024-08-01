package com.otclub.humate.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("auth/login")
    fun logIn(@Body logInRequest: LogInRequest): Call<LogInResponse>
}