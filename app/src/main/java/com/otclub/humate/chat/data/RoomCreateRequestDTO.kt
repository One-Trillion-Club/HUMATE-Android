package com.otclub.humate.chat.data

data class RoomCreateRequestDTO(
    val postId : Int,
    val writerId: String? = null
)