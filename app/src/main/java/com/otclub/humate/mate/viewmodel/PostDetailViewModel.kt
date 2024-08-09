package com.otclub.humate.mate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mate.api.PostService
import com.otclub.humate.mate.data.PostDetailResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 매칭글 상세 정보 ViewModel
 * @author 김지현
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03  	김지현        최초 생성
 * </pre>
 */
class PostDetailViewModel : ViewModel() {
    private val _postDetail = MutableLiveData<PostDetailResponseDTO>()
    val postDetail: LiveData<PostDetailResponseDTO> get() = _postDetail

    private val postService: PostService = RetrofitConnection.getInstance().create(PostService::class.java)

    /**
     * 상세 게시글 정보 조회 내용 가져오기
     */
    fun getPostDetail(postId: Int, onSuccess: (PostDetailResponseDTO) -> Unit, onError: (String) -> Unit) {

        postService.postDetail(postId).enqueue(object : Callback<PostDetailResponseDTO> {
            override fun onResponse(
                call: Call<PostDetailResponseDTO>,
                response: Response<PostDetailResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    onSuccess(response.body()!!)
                } else {
                    // 서버 응답 코드 오류 처리
                    onError("서버 오류: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostDetailResponseDTO>, t: Throwable) {
                // 네트워크 오류 등 실패 처리
                onError(t.message ?: "네트워크 오류")
            }
        })
    }
}