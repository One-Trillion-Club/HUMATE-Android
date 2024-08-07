package com.otclub.humate.chat.data

data class ChatMessageWebSocketResponseDTO(
    val chatRoomDetailDTO: ChatRoomDetailDTO,
    val chatMessage: ChatMessage
)