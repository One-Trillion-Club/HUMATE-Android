package com.otclub.humate.mate.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.mate.api.PostService
import com.otclub.humate.mate.data.PostDetailResponseDTO
import com.otclub.humate.mate.data.PostListFilterDTO
import com.otclub.humate.mate.data.PostListResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel: ViewModel() {
    private val postService: PostService = RetrofitConnection.getInstance().create(PostService::class.java)

    // 결과에 대해 상태를 저장해야 할 경우 사용
    // val logInResponseDTO = MutableLiveData<LogInResponseDTO>()

    // 필터 데이터
    var filterData: PostListFilterDTO? = null

    // 게시글 리스트를 가져오는 함수
    fun getPostList(filters: Map<String, String>, onSuccess: (List<PostListResponseDTO>) -> Unit, onError: (String) -> Unit) {

        postService.postList(filters).enqueue(object : Callback<List<PostListResponseDTO>> {
            override fun onResponse(
                call: Call<List<PostListResponseDTO>>,
                response: Response<List<PostListResponseDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // 서버 응답 성공
                    onSuccess(response.body()!!)
                } else {
                    // 서버 응답 코드 오류 처리
                    onError("서버 오류: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PostListResponseDTO>>, t: Throwable) {
                // 네트워크 오류 등 실패 처리
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    // 필터 데이터 업데이트
    fun updateFilterData(
        keyword: String?,
        tagName: String?,
        matchDate: String?,
        matchBranch: Set<String>,
        matchGender: String?,
        matchLanguage: Set<String>
    ) {
        Log.i("viewModel", "keyword = $keyword, tagName = $tagName, matchDate = $matchDate, matchBranch = $matchBranch, matchGender = $matchGender, matchLanguage = $matchLanguage")
        val formattedMatchBranch = if (matchBranch.isNotEmpty()) matchBranch.joinToString(", ") else null
        val formattedMatchLanguage = if (matchLanguage.isNotEmpty()) matchLanguage.joinToString(", ") else null

        val updatedFilterData = PostListFilterDTO(

            keyword = keyword,
            tagName = tagName,
            matchDate = matchDate,
            matchBranch = formattedMatchBranch,
            matchGender = matchGender,
            matchLanguage = formattedMatchLanguage
        )
        Log.i("viewModel", "update -> $updatedFilterData")
        filterData = updatedFilterData
    }

}