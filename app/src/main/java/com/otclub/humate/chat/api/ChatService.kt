package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.ChatCreateCompanionRequestDTO
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import com.otclub.humate.chat.data.MateUpdateRequestDTO
import com.otclub.humate.mission.data.CommonResponseDTO
import retrofit2.Call
import retrofit2.http.*

interface ChatService {

    @GET("chat/{chatRoomId}")
    fun getChatHistoryList(@Path("chatRoomId") chatRoomId: String): Call<List<ChatMessageResponseDTO>>

    @PUT("mate/update")
    fun updateMateState(@Body request: MateUpdateRequestDTO): Call<CommonResponseDTO>

    @POST("companions/start")
    fun companionStart(@Body request: ChatCreateCompanionRequestDTO): Call<CommonResponseDTO>
}