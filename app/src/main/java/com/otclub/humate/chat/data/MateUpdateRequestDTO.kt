package com.otclub.humate.chat.data

data class MateUpdateRequestDTO(
    val chatRoomId: String? = null,
    val participateId: String? = null,
    val isClicked : Int? = 0
)