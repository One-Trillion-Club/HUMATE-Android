package com.otclub.humate.chat.data

/**
 * 채팅방 상세 DTO
 * @author 최유경
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   최유경        최초 생성
 * </pre>
 */
data class RoomDetailDTO(
    // 채팅방 ID
    val chatRoomId : String,
    // 회원 ID
    val participateId : String,
    // 참여자 ID
    val memberId : String,
    // 매칭글 ID
    val postId : String,
    // 매칭글 제목
    val postTitle : String,
    // 매칭 날짜
    val matchDate : String,
    // 매칭 지점
    val matchBranch : String,
    // 상대방 메이트 맺기 클릭 여부
    val isClicked : Int,
    // 매칭 여부
    val isMatched : Int,
    // 마지막 메시지
    val latestContent: String,
    // 마지막 메시지 전송 시간
   val latestContentTime: String,
    // 상대방 닉네임
    val targetNickname : String,
    // 상대방 회원 ID
    val targetMemberId : String,
    // 상대방 참여 ID
    val targetParticipateId : String,
    // 상대방 프로필 사진
    val targetProfileImgUrl : String? = null,
    // 상대방 메이트 맺기 클릭 여부
    val targetIsClicked : Int
)