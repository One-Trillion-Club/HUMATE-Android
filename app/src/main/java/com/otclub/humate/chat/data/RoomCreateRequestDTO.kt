package com.otclub.humate.chat.data

/**
 * 채팅방 생성 요청 DTO
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
data class RoomCreateRequestDTO(
    // 매칭글 ID
    val postId : Int
)