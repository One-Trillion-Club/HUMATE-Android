package com.otclub.humate.chat.data

import android.content.Context
import com.otclub.humate.R

/**
 * 채팅메세지 타입 Enum
 * @author 최유경
 * @since 2024.08.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.07  	최유경        최초 생성
 * 2024.08.08   최유경        메이트 관련 데이터 추가
 * </pre>
 */
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

    companion object {
        fun fromType(type: Int): MessageType? {
            return values().find { it.type == type }
        }
    }
}