package com.otclub.humate.chat.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ChatWebSocketListener(private val fragment: ChatFragment) : WebSocketListener() {
    private val gson = Gson()
    private val handler = Handler(Looper.getMainLooper())

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("WebSocket", "WebSocket 종료됨: 코드=$code, 이유=$reason")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d("WebSocket", "WebSocket 종료 중: 코드=$code, 이유=$reason")
        webSocket.close(code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e("WebSocket", "WebSocket 실패: ${t.message}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d("WebSocket", "onMessage 수신된 메시지: $text")
        val response = gson.fromJson(text, ChatMessageResponseDTO::class.java)
        handler.post {
            fragment.updateChat(response)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d("WebSocket", "onMessage 수신된 메시지: $bytes")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d("WebSocket", "WebSocket 연결이 성공적으로 열림. Response: $response")
    }
}