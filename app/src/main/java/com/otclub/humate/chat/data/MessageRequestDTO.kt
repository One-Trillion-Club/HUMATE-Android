package com.otclub.humate.chat.data

data class MessageRequestDTO(
    val chatRoomId : String?,
    val participateId : String?,
    val content : String?,
    val messageType : MessageType
)
