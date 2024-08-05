package com.otclub.humate.chat.data

data class ChatMessageRequestDTO(
    val chatRoomId : String?,
    val participateId : String?,
    val content : String?,
    val messageType : MessageType
)
