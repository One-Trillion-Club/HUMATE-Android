package com.otclub.humate.chat.data

data class ChatMessageRequestDTO(
    val chatRoomId : String,
    val senderId : String,
    val content : String,
    val messageType : MessageType
)
