package com.otclub.humate.chat.data

data class MessageWebSocketResponseDTO(
    val roomDetailDTO: RoomDetailDTO,
    val message: Message
)