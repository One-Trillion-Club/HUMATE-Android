package com.otclub.humate.chat.data

/**
 * 채팅방 생성 응답 DTO
 * @author 최유경
 * @since 2024.08.08
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.08  	최유경        최초 생성
 * </pre>
 */
data class RoomCreateResponseDTO(
    // 채팅방 ID
    val chatRoomId: String? = null,
    // 참여 ID
    val participateId: String? = null
)
