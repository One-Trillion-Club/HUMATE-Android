package com.otclub.humate.member.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.otclub.humate.auth.api.AuthService
import com.otclub.humate.auth.data.CommonResponseDTO
import com.otclub.humate.mate.data.MateDetailResponseDTO
import com.otclub.humate.member.api.MemberService
import com.otclub.humate.member.data.ModifyProfileRequestDTO
import com.otclub.humate.member.data.ProfileResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback

class MemberViewModel(application: Application): AndroidViewModel(application) {
    private val authService: AuthService =
        RetrofitConnection.getInstance().create(AuthService::class.java)
    private val memberService: MemberService =
        RetrofitConnection.getInstance().create(MemberService::class.java)
    private val sharedPreferencesManager: SharedPreferencesManager =
        SharedPreferencesManager(application)

    val profileResponseDTO = MutableLiveData<ProfileResponseDTO>()
    val modifyProfileRequestDTO = MutableLiveData<ModifyProfileRequestDTO>()

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

    fun fetchGetMyProfile(
        onSuccess: (ProfileResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        memberService.getMyProfile().enqueue(object : Callback<ProfileResponseDTO> {
            override fun onResponse(
                call: Call<ProfileResponseDTO>,
                response: retrofit2.Response<ProfileResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    profileResponseDTO.value = response.body()
                    onSuccess(response.body()!!)
                }
                else {
                    Log.i("MemberViewModel: fetchGetMyProfile Error", response.toString())
                }
            }

            override fun onFailure(call: Call<ProfileResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    fun fetchModifyProfile(
        dto: ModifyProfileRequestDTO,
        image: MultipartBody.Part?,
        onSuccess: (CommonResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        memberService.modifyProfile(dto, image).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: retrofit2.Response<CommonResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    onSuccess(response.body()!!)
                } else {
                    onError("회원 수정 실패. ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    fun fetchGetMyMateList(
        onSuccess: (List<MateDetailResponseDTO>) -> Unit,
        onError: (String) -> Unit
    ) {
        memberService.getMyMateList().enqueue(object : Callback<List<MateDetailResponseDTO>> {
            override fun onResponse(
                call: Call<List<MateDetailResponseDTO>>,
                response: retrofit2.Response<List<MateDetailResponseDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    onSuccess(response.body()!!)
                }
                else {
                    Log.i("MemberViewModel: getMyMateList Error", response.toString())
                }
            }

            override fun onFailure(call: Call<List<MateDetailResponseDTO>>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    fun getOtherMemberProfile(
        memberId: String,
        onSuccess: (ProfileResponseDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        memberService.getOtherMemberProfile(memberId).enqueue(object : Callback<ProfileResponseDTO> {
            override fun onResponse(
                call: Call<ProfileResponseDTO>,
                response: retrofit2.Response<ProfileResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    onSuccess(response.body()!!)
                }
                else {
                    Log.i("MemberViewModel: getMyMateList Error", response.toString())
                }
            }

            override fun onFailure(call: Call<ProfileResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }
}