package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.RoomCreateRequestDTO
import com.otclub.humate.chat.data.RoomCreateResponseDTO
import com.otclub.humate.chat.data.RoomDetailDTO
import retrofit2.Call
import retrofit2.http.*

interface ChatMainService {
    @GET("rooms/list")
    fun getChatRoomList(): Call<List<RoomDetailDTO>>

    @GET("rooms/list/pending")
    fun getPendingChatRoomList(): Call<List<RoomDetailDTO>>

    @POST("rooms/create")
    fun createChatRoom(@Body request : RoomCreateRequestDTO): Call<RoomCreateResponseDTO>
}


