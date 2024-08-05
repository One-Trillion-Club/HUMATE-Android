package com.otclub.humate.chat.data

enum class MessageType(val type: Int) {
    TEXT(0),
    IMAGE(1),
    ENTER(2),
    EXIT(3),
    MATE_ACTIVE(4),
    MATE_INACTIVE(5);
}