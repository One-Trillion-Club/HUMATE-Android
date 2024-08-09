package com.otclub.humate.chat.data

/**
 * 웹소켓 메세지 실시간 전송을 위한 DTO
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
data class MessageWebSocketResponseDTO(
    // 채팅방 상세 DTO
    val roomDetailDTO: RoomDetailDTO,
    // 채팅 메세지
    val message: Message
)