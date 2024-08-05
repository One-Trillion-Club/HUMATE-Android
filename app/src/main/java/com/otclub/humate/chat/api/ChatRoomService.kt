package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.ChatRoomDetailDTO
import retrofit2.Call
import retrofit2.http.*

interface ChatRoomService {
    @GET("rooms/list/{memberId}")
    fun getChatRoomList(@Path("memberId") memberId: String): Call<List<ChatRoomDetailDTO>>

    @GET("rooms/list/pending/{memberId}")
    fun getPendingChatRoomList(@Path("memberId") memberId: String): Call<List<ChatRoomDetailDTO>>
}


