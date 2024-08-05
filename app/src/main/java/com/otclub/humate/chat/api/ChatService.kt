package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.ChatMessageResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {

    @GET("/chat/{chatRoomId}")
    fun getChatHistoryList(@Path("chatRoomId") chatRoomId: String): Call<List<ChatMessageResponseDTO>>
}