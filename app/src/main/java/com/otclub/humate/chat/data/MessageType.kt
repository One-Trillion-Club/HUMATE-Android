package com.otclub.humate.chat.data

import android.content.Context
import com.otclub.humate.R

enum class MessageType(val type: Int, val resId: Int) {
    TEXT(0, R.string.chat_default_message),
    IMAGE(1, R.string.chat_default_message),
    ENTER(2, R.string.chat_default_message),
    EXIT(3, R.string.chat_default_message),
    MATE_ACTIVE(4, R.string.chat_mate_active_message),
    MATE_INACTIVE(5, R.string.chat_mate_inactive_message);

    fun getMessage(context: Context): String {
        return context.getString(resId)
    }
}