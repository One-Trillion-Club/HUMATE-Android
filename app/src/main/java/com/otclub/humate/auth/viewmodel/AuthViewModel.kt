package com.otclub.humate.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.auth.api.AuthService
import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.auth.data.GeneratePhoneCodeRequestDTO
import com.otclub.humate.auth.data.LogInRequestDTO
import com.otclub.humate.auth.data.LogInResponseDTO
import com.otclub.humate.auth.data.SendPhoneCodeRequestDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback

class AuthViewModel: ViewModel() {
    private val authService: AuthService = RetrofitConnection.getInstance().create(AuthService::class.java)

    // 결과에 대해 상태를 저장해야 할 경우 사용
    // val logInResponseDTO = MutableLiveData<LogInResponseDTO>()

    fun fetchLogIn(dto: LogInRequestDTO, onSuccess: (CommonResponseDTO) -> Unit, onError: (String) -> Unit) {
        authService.logIn(dto).enqueue(object: Callback<CommonResponseDTO> {
            override fun onResponse(call: Call<CommonResponseDTO>, response: retrofit2.Response<CommonResponseDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    Log.i("sign in btn click success", response.body().toString())
                    onSuccess(response.body()!!)
                } else {
                    onError("아이디나 비밀번호가 틀렸습니다.")
                }
            }
            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    fun fetchGeneratePhoneCode(dto: GeneratePhoneCodeRequestDTO, onSuccess: (CommonResponseDTO) -> Unit, onError: (String) -> Unit) {
        authService.generatePhoneCode(dto).enqueue(object: Callback<CommonResponseDTO> {
            override fun onResponse(call: Call<CommonResponseDTO>, response: retrofit2.Response<CommonResponseDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("인증번호 생성에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })

    }

    fun fetchSendPhoneCode(dto: SendPhoneCodeRequestDTO, onSuccess: (CommonResponseDTO) -> Unit, onError: (String) -> Unit) {
        authService.sendPhoneCode(dto).enqueue(object: Callback<CommonResponseDTO> {
            override fun onResponse(call: Call<CommonResponseDTO>, response: retrofit2.Response<CommonResponseDTO>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {

                    onSuccess(response.body()!!)
                } else {
                    onError("인증 실패.")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })

    }

}