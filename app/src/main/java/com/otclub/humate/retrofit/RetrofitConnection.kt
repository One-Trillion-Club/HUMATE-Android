package com.otclub.humate.retrofit

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class RetrofitConnection {

    // 싱글턴 패턴 적용
    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"
        private var INSTANCE: Retrofit? = null

        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        fun getInstance(): Retrofit {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}