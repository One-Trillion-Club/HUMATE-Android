package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.CompanionCreateRequestDTO
import com.otclub.humate.chat.data.Message
import com.otclub.humate.chat.data.MessageWebSocketResponseDTO
import com.otclub.humate.chat.data.MateUpdateRequestDTO
import com.otclub.humate.mission.data.CommonResponseDTO
import retrofit2.Call
import retrofit2.http.*

/**
 * 1:1 채팅 Service
 *
 * @author 최유경
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04  	최유경        최초 생성
 * 2024.08.07   최유경        메이트 맺기 신청/취소, 동행 생성
 * </pre>
 */
interface ChatService {

    /**
     * 채팅 과거 내역 리스트 조회 요청
     */
    @GET("chat/{chatRoomId}")
    fun getChatHistoryList(@Path("chatRoomId") chatRoomId: String): Call<List<Message>>

    /**
     * 채팅 과거 내역 리스트 조회 요청
     */
    @GET("chat/history/{chatRoomId}")
    fun getChatMessageHistoryList(@Path("chatRoomId") chatRoomId: String): Call<MessageWebSocketResponseDTO>

    /**
     * 메이트 맺기/취소 요청
     */
    @PUT("mate/update")
    fun updateMateState(@Body request: MateUpdateRequestDTO): Call<CommonResponseDTO>

    /**
     * 동행 시작 요청
     */
    @POST("companions/start")
    fun companionStart(@Body request: CompanionCreateRequestDTO): Call<CommonResponseDTO>
}