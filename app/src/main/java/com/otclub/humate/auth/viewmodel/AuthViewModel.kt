package com.otclub.humate.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.auth.api.AuthService
import com.otclub.humate.auth.data.LogInRequestDTO
import com.otclub.humate.auth.data.LogInResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback

class AuthViewModel: ViewModel() {
    private val authService: AuthService = RetrofitConnection.getInstance().create(AuthService::class.java)

    // 결과에 대해 상태를 저장해야 할 경우 사용
    // val logInResponseDTO = MutableLiveData<LogInResponseDTO>()

    fun fetchLogIn(dto: LogInRequestDTO, onSuccess: (LogInResponseDTO) -> Unit, onError: (String) -> Unit) {
        authService.logIn(dto).enqueue(object: Callback<LogInResponseDTO> {
            override fun onResponse(call: Call<LogInResponseDTO>, response: retrofit2.Response<LogInResponseDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    Log.i("sign in btn click success", response.body().toString())
                    onSuccess(response.body()!!)
                } else {
                    // 서버 응답 코드 오류 처리
                    Log.i("login", "아이디나 비번 틀림")
                    onError("아이디나 비밀번호가 틀렸습니다.")
                }
            }
            override fun onFailure(call: Call<LogInResponseDTO>, t: Throwable) {
                // 네트워크 오류 등 실패 처리
                Log.i("(API 요청 실패) login", t.toString())
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

}