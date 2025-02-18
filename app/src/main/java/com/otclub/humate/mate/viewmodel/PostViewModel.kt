package com.otclub.humate.mate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.otclub.humate.mate.api.PostService
import com.otclub.humate.mate.data.PostListFilterDTO
import com.otclub.humate.mate.data.PostListResponseDTO
import com.otclub.humate.member.api.MemberService
import com.otclub.humate.member.data.ProfileResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 매칭글 전체 조회 ViewModel
 * @author 김지현
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	김지현        최초 생성
 * 2024.08.03   김지현        필터 내용 업데이트 메서드 추가
 * 2024.08.08   김지현        memberId 가져오는 메서드 추가
 * </pre>
 */
class PostViewModel: ViewModel() {
    private val postService: PostService = RetrofitConnection.getInstance().create(PostService::class.java)
    private val memberService: MemberService = RetrofitConnection.getInstance().create(MemberService::class.java)

    // 필터 데이터
    var filterData: PostListFilterDTO? = null

    /**
     * memberId 서버로부터 가져오기
     */
    fun fetchMemberId(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        memberService.getMyProfile().enqueue(object : Callback<ProfileResponseDTO> {
            override fun onResponse(call: Call<ProfileResponseDTO>, response: Response<ProfileResponseDTO>) {
                if (response.isSuccessful) {
                    response.body()?.memberId?.let {
                        onSuccess(it)
                    } ?: onError("memberId를 가져올 수 없습니다.")
                } else {
                    onError("서버 오류: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponseDTO>, t: Throwable) {
                onError(t.message ?: "네트워크 오류")
            }
        })
    }

    /**
     * 매칭글 전체 조회 리스트 가져오기
     */
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

    /**
     * 필터링 데이터 업데이트
     */
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