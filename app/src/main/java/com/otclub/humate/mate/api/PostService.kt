package com.otclub.humate.mate.api

import com.otclub.humate.mate.data.PostDetailResponseDTO
import com.otclub.humate.mate.data.PostListResponseDTO
import com.otclub.humate.mate.data.PostWriteRequestDTO
import retrofit2.Call
import retrofit2.http.*

interface PostService {
    @GET("posts/list")
    fun postList(@QueryMap filters: Map<String, String>): Call<List<PostListResponseDTO>>

    @GET("posts/{postId}")
    fun postDetail(@Path("postId") postId: Int): Call<PostDetailResponseDTO>

    @POST("posts/new")
    fun postAdd(@Body request: PostWriteRequestDTO): Call<Int>
}