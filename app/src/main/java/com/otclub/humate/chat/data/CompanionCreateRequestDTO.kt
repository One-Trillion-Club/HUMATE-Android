package com.otclub.humate.chat.data

/**
 * 동행 생성을 위한 DTO
 * @author 손승완
 * @since 2024.08.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.07  	손승완        최초 생성
 * </pre>
 */
data class CompanionCreateRequestDTO(
    // 채팅방 ID
    var chatRoomId: String? = null
)
