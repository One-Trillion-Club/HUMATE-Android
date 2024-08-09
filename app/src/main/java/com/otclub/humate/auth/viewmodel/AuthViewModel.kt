package com.otclub.humate.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.otclub.humate.auth.api.AuthService
import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.auth.data.GeneratePhoneCodeRequestDTO
import com.otclub.humate.auth.data.LoginRequestDTO
import com.otclub.humate.auth.data.SignUpRequestDTO
import com.otclub.humate.auth.data.VerifyPassportRequestDTO
import com.otclub.humate.auth.data.VerifyPhoneCodeRequestDTO
import com.otclub.humate.member.api.MemberService
import com.otclub.humate.retrofit.RetrofitConnection
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback

/**
 * 인증/인가 View Model
 * @author 조영욱
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	조영욱        최초 생성
 * 2024.08.03  	조영욱        휴대전화 인증 추가
 * 2024.08.06  	조영욱        여권번호 인증 추가
 * </pre>
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authService: AuthService =
        RetrofitConnection.getInstance().create(AuthService::class.java)
    private val memberService: MemberService =
        RetrofitConnection.getInstance().create(MemberService::class.java)

    // 회원가입 Request 정보 저장
    val signUpRequestDTO: SignUpRequestDTO = SignUpRequestDTO()
    private val sharedPreferencesManager: SharedPreferencesManager =
        SharedPreferencesManager(application)


    /**
     * 회원 로그인
     */
    fun fetchLogIn(
        dto: LoginRequestDTO,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.logIn(dto).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    sharedPreferencesManager.setIsLogin(true)

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

    /**
     * 휴대전화 인증 코드 생성 요청
     */
    fun fetchGeneratePhoneCode(
        dto: GeneratePhoneCodeRequestDTO,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.generatePhoneCode(dto).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
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

    /**
     * 휴대전화 인증 코드 검증 요청
     */
    fun fetchVerifyPhoneCode(
        dto: VerifyPhoneCodeRequestDTO,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.verifyPhoneCode(dto).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
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

    /**
     * 로그인 아이디 중복 확인 요청
     */
    fun fetchCheckLoginId(
        loginId: String,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.checkLoginId(loginId).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    onSuccess(response.body()!!)
                } else {
                    onError("이미 사용중인 아이디입니다.")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    /**
     * 닉네임 중복 확인 요청
     */
    fun fetchCheckNickname(
        nickname: String,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        memberService.checkNickname(nickname).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    onSuccess(response.body()!!)
                } else {
                    onError("이미 사용중인 닉네임입니다.")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    /**
     * 회원가입 요청
     */
    fun signUp(
        dto: SignUpRequestDTO,
        image: MultipartBody.Part?,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.signUp(dto, image).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    onSuccess(response.body()!!)
                } else {
                    onError("회원가입에 실패하였습니다. ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    /**
     * 여권번호 검증 요청
     */
    fun fetchVerifyPassport(
        dto: VerifyPassportRequestDTO,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.verifyPassport(dto).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    onSuccess(response.body()!!)
                } else {
                    onError("인증 실패")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }
}