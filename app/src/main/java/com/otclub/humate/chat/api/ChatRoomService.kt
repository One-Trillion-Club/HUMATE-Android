package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.ChatRoomDetailDTO
import retrofit2.Call
import retrofit2.http.*

interface ChatRoomService {
    @GET("rooms/list")
    fun getChatRoomList(): Call<List<ChatRoomDetailDTO>>

    @GET("rooms/list/pending")
    fun getPendingChatRoomList(): Call<List<ChatRoomDetailDTO>>
}


