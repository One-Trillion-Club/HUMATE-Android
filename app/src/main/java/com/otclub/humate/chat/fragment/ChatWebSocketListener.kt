package com.otclub.humate.chat.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.otclub.humate.chat.data.MessageWebSocketResponseDTO
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * 웹소켓 Listener
 * @author 최유경
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   최유경        최초 생성
 * 2024.08.08   최유경        웹소켓 response dto 변경
 * </pre>
 */
class ChatWebSocketListener(private val fragment: ChatFragment) : WebSocketListener() {
    private val gson = Gson()
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 웹소켓 연결 종료
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("WebSocket", "WebSocket 종료됨: 코드=$code, 이유=$reason")
    }

    /**
     * 엡소켓 연결 종료 처리 중
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d("WebSocket", "WebSocket 종료 중: 코드=$code, 이유=$reason")
        webSocket.close(code, reason)
    }

    /**
     * 웹소켓 연결 실패
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e("WebSocket", "WebSocket 실패: ${t.message}")
    }

    /**
     * 웹소켓 메세지 수신
     */
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d("WebSocket", "onMessage 수신된 메시지: $text")
        val response = gson.fromJson(text, MessageWebSocketResponseDTO::class.java)
        handler.post {
            fragment.updateChat(response)
        }
    }


    /**
     * 웹소켓 메세지 수신
     */
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d("WebSocket", "onMessage 수신된 메시지: $bytes")
    }

    /**
     * 웹소켓 연결
     */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d("WebSocket", "WebSocket 연결이 성공적으로 열림. Response: $response")
    }
}