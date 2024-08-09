package com.otclub.humate.chat.data

/**
 * 채팅방에서 메이트 맺을 때 사용하는 DTO
 * @author 최유경
 * @since 2024.08.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.07   최유경        최초 생성
 * </pre>
 */
data class MateUpdateRequestDTO(
    // 채팅방 ID
    val chatRoomId: String? = null,
    // 참여자 ID
    val participateId: String? = null,
    // 메이트 맺기 여부
    val isClicked : Int? = 0
)