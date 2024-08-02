package com.otclub.humate.chat.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.otclub.humate.BuildConfig.*
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adpater.ChatAdapter
import com.otclub.humate.chat.data.ChatMessageRequestDTO
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import com.otclub.humate.chat.data.MessageType
import com.otclub.humate.databinding.ChatFragmentBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import java.util.concurrent.TimeUnit

class ChatFragment : Fragment() {

    private var mBinding : ChatFragmentBinding? = null
    private lateinit var webSocketListener: ChatWebSocketListener
    private lateinit var client: OkHttpClient
    private var webSocket : WebSocket ?= null
    private val handler = Handler(Looper.getMainLooper())
    private val reconnectRunnable = object : Runnable {
        override fun run() {
            Log.d("WebSocket", "웹소켓 재연결 요청")
            handler.postDelayed(this, 180000) // 3분마다 재연결 요청
            closeWebSocket()
            startWebSocket()
        }
    }

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessageResponseDTO>()
    private val myId = TEST_MEMBER_2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = ChatFragmentBinding.inflate(inflater, container, false)
        mBinding = binding

        // RecyclerView 설정
        chatAdapter = ChatAdapter(messages, myId)
        mBinding?.chatDisplay?.adapter = chatAdapter
        mBinding?.chatDisplay?.layoutManager = LinearLayoutManager(requireContext())

        return mBinding?.root
    }

    override fun onResume() {
        super.onResume()
        handler.post(reconnectRunnable) // Start periodic reconnection
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(reconnectRunnable)
        closeWebSocket()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupSendButton()
    }

    private fun setupToolbar(){
        val activity = activity as? MainActivity
        activity?.let {
            val toolbar = it.getToolbar() // MainActivity의 Toolbar를 가져옴
            val leftButton: ImageButton = toolbar.findViewById(R.id.left_button)
            val rightButton: Button = toolbar.findViewById(R.id.right_button)

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = false
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            rightButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // 액션 바의 타이틀을 설정하거나 액션 바의 다른 속성을 조정
            it.setToolbarTitle("채팅")
        }
    }
    private fun setupSendButton() {
        mBinding?.sendButton?.setOnClickListener {
            val message = mBinding?.messageInput?.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                mBinding?.messageInput?.text?.clear()
            }
        }
    }

    private fun sendMessage(content: String) {
        val messageRequest = ChatMessageRequestDTO(
            chatRoomId = "10",
            senderId = TEST_MEMBER_1, // 실제 사용자 ID로 변경
            content = content,
            messageType = MessageType.TEXT
        )

        val gson = Gson()
        val messageJson = gson.toJson(messageRequest)
        webSocket?.send(messageJson)

        // ChatAdapter에 메시지 추가
        val sentMessage = ChatMessageResponseDTO(
            chatRoomId = "10",
            senderId = TEST_MEMBER_2,
            content = content,
            messageType = MessageType.TEXT,
            createdAt = Date()
        )
        chatAdapter.addMessage(sentMessage)
    }

    fun updateChat(message: ChatMessageResponseDTO) {
        chatAdapter.addMessage(message)
    }

    private fun startWebSocket(){

        client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .header("authorization", "K_1")
            .build()

        webSocketListener = ChatWebSocketListener(this)
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private fun closeWebSocket() {
        webSocket?.close(1000, "[closeWebSocket] - Fragment is pausing")
        webSocket = null
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}