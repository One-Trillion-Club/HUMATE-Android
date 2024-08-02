package com.otclub.humate.chat

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.util.*

class ChatWebSocketClient(serverUri: URI, private val headers: Map<String, String>) : WebSocketClient(serverUri, headers) {
    private var reconnectTimer : Timer? = null
    private val reconnectDelay : Long = 5000
    private var isConnected = false

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("WebSocketClient","Connected to server")
        isConnected = true
        reconnectTimer?.cancel()
    }

    override fun onMessage(message: String?) {
        Log.d("WebSocketClient","Received message: $message")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WebSocketClient","DisConnected from server")
        isConnected = false
    }

    override fun onError(ex: Exception?) {
        Log.d("WebSocketClient","${ex?.message}")
    }

    private fun scheduleReconnect(){
        if(isConnected) return

        reconnectTimer?.cancel()
        reconnectTimer = Timer().apply {
            schedule(object : TimerTask(){
                override fun run() {
                    reconnect()
                }
            }, reconnectDelay)
        }
    }

//    private fun reconnect(){
//        try{
//            if(!this.isOpen){
//                Log.d("WebSocketClient","재연결 시도합니다!")
//                this.reconnect()
//            }
//        }
//    }
}