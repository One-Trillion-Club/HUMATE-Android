package com.otclub.humate.retrofit

import android.content.Context
import android.util.Log
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import okhttp3.Response
import okhttp3.Interceptor
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
        private lateinit var sharedPreferencesManager: SharedPreferencesManager

        fun init(context: Context) {
            sharedPreferencesManager = SharedPreferencesManager(context)
        }

        private fun createClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(CookieManager()))
                .addInterceptor(CookieInterceptor(sharedPreferencesManager))
                .addInterceptor(RequestInterceptor(sharedPreferencesManager))
                .build()
        }

        fun getInstance(): Retrofit {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .client(createClient())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}

class CookieInterceptor(private val sharedPreferencesManager: SharedPreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Set-Cookie 헤더 확인
        val cookies = response.headers("Set-Cookie")
        if (cookies.isNotEmpty()) {
            var accessToken: String? = null
            var refreshToken: String? = null

            for (cookie in cookies) {
                val parsedCookie = cookie.split(";").map { it.trim() }

                for (keyValue in parsedCookie) {
                    when {
                        keyValue.startsWith("ajt=") -> accessToken = keyValue.substringAfter("ajt=")
                        keyValue.startsWith("rjt=") -> refreshToken = keyValue.substringAfter("rjt=")
                    }
                }
            }

            if (accessToken != null && refreshToken != null) {
                sharedPreferencesManager.setLoginToken(accessToken, refreshToken)
            }
        }

        return response
    }
}

class RequestInterceptor(private val sharedPreferencesManager: SharedPreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val (accessToken, refreshToken) = sharedPreferencesManager.getLoginToken()

        if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
            requestBuilder.addHeader("Cookie", "ajt=$accessToken; rjt=$refreshToken")
        } else {
            sharedPreferencesManager.clear()
            sharedPreferencesManager.setIsLogin(false)
        }

        return chain.proceed(requestBuilder.build())
    }
}