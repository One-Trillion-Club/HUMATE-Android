package com.otclub.humate.chat.data

/**
 * 채팅메세지 MongoDB Document
 * @author 최유경
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   	최유경        최초 생성
 * </pre>
 */

data class Message(
    // 채팅방 ID
    val chatRoomId : String,
    // 참여 ID
    val participateId : String?,
    // 채팅 내용
    val content : String,
    // 보낸 날짜
    val createdAt : String,
    // 읽음 여부
    val readCnt : Int,
    // 채팅 유형
    val messageType : MessageType,
    // 채팅 이미지 내용
    val imgUrl : String
)