package com.otclub.humate.chat.api

import com.otclub.humate.chat.data.RoomCreateRequestDTO
import com.otclub.humate.chat.data.RoomCreateResponseDTO
import com.otclub.humate.chat.data.RoomDetailDTO
import retrofit2.Call
import retrofit2.http.*

/**
 * 채팅 메인 Service
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
 * 2024.08.08   최유경        채팅방 생성 추가
 * </pre>
 */
interface ChatMainService {
    /**
     * 메이트 채팅 리스트 조회 요청
     */
    @GET("rooms/list")
    fun getChatRoomList(): Call<List<RoomDetailDTO>>

    /**
     * 대기 채팅 리스트 조회 요청
     */
    @GET("rooms/list/pending")
    fun getPendingChatRoomList(): Call<List<RoomDetailDTO>>

    /**
     * 채팅방 생성 요청
     */
    @POST("rooms/create")
    fun createChatRoom(@Body request : RoomCreateRequestDTO): Call<RoomCreateResponseDTO>
}


