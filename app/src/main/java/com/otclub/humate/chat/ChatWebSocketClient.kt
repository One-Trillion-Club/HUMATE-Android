package com.otclub.humate.chat

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class ChatWebSocketClient(serverUri: URI, private val headers: Map<String, String>) : WebSocketClient(serverUri, headers) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("WebSocketClient","Connected to server")
    }

    override fun onMessage(message: String?) {
        Log.d("WebSocketClient","Received message: $message")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WebSocketClient","DisConnected from server")
    }

    override fun onError(ex: Exception?) {
        Log.d("WebSocketClient","${ex?.message}")
    }
}