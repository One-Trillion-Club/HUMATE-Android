package com.otclub.humate.member.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.otclub.humate.auth.api.AuthService
import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.member.api.MemberService
import com.otclub.humate.retrofit.RetrofitConnection
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback

class MemberViewModel(application: Application): AndroidViewModel(application) {
    private val authService: AuthService =
        RetrofitConnection.getInstance().create(AuthService::class.java)
    private val memberService: MemberService =
        RetrofitConnection.getInstance().create(MemberService::class.java)
    private val sharedPreferencesManager: SharedPreferencesManager =
        SharedPreferencesManager(application)

    /**
     * 로그아웃
     */
    fun fetchLogout(
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.logOut().enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    sharedPreferencesManager.setIsLogin(false)
                    onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }
}