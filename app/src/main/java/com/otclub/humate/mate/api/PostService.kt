package com.otclub.humate.mate.api

import com.otclub.humate.mate.data.PostListResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface PostService {
    @GET("posts/list")
    fun postList(@QueryMap filters: Map<String, String>): Call<List<PostListResponseDTO>>
}