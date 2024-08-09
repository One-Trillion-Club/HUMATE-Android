package com.otclub.humate.mate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.otclub.humate.mate.api.PostService
import com.otclub.humate.mate.data.PostWriteOptionDTO
import com.otclub.humate.mate.data.PostWritePlaceRequestDTO
import com.otclub.humate.mate.data.PostWriteRequestDTO
import com.otclub.humate.mate.data.PostWriteTagRequestDTO
import com.otclub.humate.member.api.MemberService
import com.otclub.humate.member.data.ProfileResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 매칭글 작성 ViewModel
 * @author 김지현
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------------------
 * 2024.08.04  	김지현        최초 생성
 * 2024.08.05   김지현        매칭글 작성 및 옵션 데이터 업데이트 메서드 추가
 * 2024.08.08   김지현        memberId 가져오는 메서드 추가
 * </pre>
 */
class PostWriteViewModel: ViewModel() {
    private val postService: PostService = RetrofitConnection.getInstance().create(PostService::class.java)
    private val memberService: MemberService = RetrofitConnection.getInstance().create(MemberService::class.java)

    // 옵션 데이터
    var optionData: PostWriteOptionDTO? = null

    // 게시글 작성 요청 데이터
    var requestData: PostWriteRequestDTO? =  null

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
     * 매칭글 작성
     */
    fun writePost(onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        postService.postAdd(requestData!!).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    response.body()?.let { postId ->
                        onSuccess(postId)

                        // optionData와 requestData 초기화
                        optionData = null
                        requestData = null
                    } ?: onError("응답 본문이 비어 있습니다.")
                } else {
                    onError("오류 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                onError("요청 실패: ${t.message}")
            }
        })
    }

    /**
     * 선택 옵션(매칭 정보) 업데이트
     */
    fun updateOptionData(
        matchDate: String?,
        matchBranch: Set<String>,
        matchGender: Int?,
        matchLanguage: Set<String>
    ) {
        Log.i("viewModel", "matchDate = $matchDate, matchBranch = $matchBranch, matchGender = $matchGender, matchLanguage = $matchLanguage")
        val formattedMatchBranch = if (matchBranch.isNotEmpty()) matchBranch.joinToString(", ") else null
        val formattedMatchLanguage = if (matchLanguage.isNotEmpty()) matchLanguage.joinToString(", ") else null

        val updatedOptionData = PostWriteOptionDTO(
            matchDate = matchDate,
            matchBranch = formattedMatchBranch,
            matchGender = matchGender,
            matchLanguage = formattedMatchLanguage
        )
        Log.i("viewModel", "update -> $updatedOptionData")
        optionData = updatedOptionData
    }

    /**
     * requestDTO 업데이트
     */
    fun updateRequestData(
        memberId: String?,
        title: String?,
        content: String?,
        matchDate: String?,
        matchBranch: Set<String>,
        matchGender: Int?,
        matchLanguage: Set<String>,
        postPlaces: List<PostWritePlaceRequestDTO>,
        postTags: List<PostWriteTagRequestDTO>
    ) {
        val formattedMatchBranch = if (matchBranch.isNotEmpty()) matchBranch.joinToString(", ") else null
        val formattedMatchLanguage = if (matchLanguage.isNotEmpty()) matchLanguage.joinToString(", ") else null

        val postPlacesList = postPlaces.toList()
        val postTagsList = postTags.toList()

        val updatedRequestData = PostWriteRequestDTO(
            postId = null,
            memberId = memberId,
            title = title,
            content = content,
            matchDate = matchDate,
            matchBranch = formattedMatchBranch,
            matchGender = matchGender,
            matchLanguage = formattedMatchLanguage,
            postPlaces = postPlacesList,
            postTags = postTagsList
        )
        Log.i("viewModelRequest", "update -> $updatedRequestData")
        requestData = updatedRequestData
    }
}