package com.otclub.humate.mate.api

import com.otclub.humate.mate.data.PostDetailResponseDTO
import com.otclub.humate.mate.data.PostListResponseDTO
import com.otclub.humate.mate.data.PostWriteRequestDTO
import retrofit2.Call
import retrofit2.http.*

/**
 * 매칭글 Service
 * @author 김지현
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	김지현        최초 생성
 * 2024.08.03   김지현        매칭글 정보 상세 조회 메서드 추가
 * 2024.08.05   김지현        매칭글 등록 메서드 추가
 * </pre>
 */
interface PostService {

    /**
     * 매칭글 목록 조회
     */
    @GET("posts/list")
    fun postList(@QueryMap filters: Map<String, String>): Call<List<PostListResponseDTO>>

    /**
     * 매칭글 정보 상세 조회
     */
    @GET("posts/{postId}")
    fun postDetail(@Path("postId") postId: Int): Call<PostDetailResponseDTO>

    /**
     * 매칭글 등록
     */
    @POST("posts/new")
    fun postAdd(@Body request: PostWriteRequestDTO): Call<Int>
}