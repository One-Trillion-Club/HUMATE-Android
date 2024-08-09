package com.otclub.humate.chat.data

/**
 * 채팅 메세지 요청 DTO
 * @author 최유경
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	최유경        최초 생성
 * </pre>
 */

data class MessageRequestDTO(
    // 채팅방 ID
    val chatRoomId : String?,
    // 참여자 ID
    val participateId : String?,
    // 채팅 내용
    val content : String?,
    // 채팅 유형
    val messageType : MessageType
)
