package com.otclub.humate.chat.data

data class ChatSendMessageRequestDTO(
    val chatRoomId : String?,
    val participateId : String?,
    val content : String?,
    val messageType : MessageType
)
