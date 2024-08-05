package com.otclub.humate.chat.data

import java.util.*

data class ChatMessageResponseDTO(
    val chatRoomId : String,
    val participateId : String?,
    val content : String,
    val createdAt : String,
    val readCnt : Int,
    val messageType : MessageType,
    val imgUrl : String
)