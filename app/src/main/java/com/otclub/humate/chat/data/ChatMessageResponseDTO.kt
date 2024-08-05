package com.otclub.humate.chat.data

import java.util.*

data class ChatMessageResponseDTO(
    val chatRoomId : String,
    val senderId : String,
    val content : String,
    val messageType : MessageType,
    val createdAt : String
)
