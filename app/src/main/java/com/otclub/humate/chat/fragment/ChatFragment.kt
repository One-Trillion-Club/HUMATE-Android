package com.otclub.humate.chat.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.otclub.humate.BuildConfig.*
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.ChatAdapter
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
    private val myId = TEST_MEMBER_1

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar() // `view`를 인자로 전달할 필요 없음
        setupSendButton()
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
            senderId = TEST_MEMBER_1,
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
        (activity as? MainActivity)?.restoreToolbar()
        mBinding = null
        super.onDestroyView()
    }

    private fun setupToolbar() {
//        val activity = activity as? MainActivity
//        activity?.let {
//            // 기존 Toolbar 숨기기
//            val mainToolbar = it.getToolbar()
//            mainToolbar?.visibility = View.GONE
//
//            // 새로운 Toolbar 설정
//            val chatToolbar = LayoutInflater.from(context).inflate(R.layout.chat_toolbar, null) as Toolbar
//            val leftButton: ImageButton = chatToolbar.findViewById(R.id.left_button)
//            val menuButton: ImageButton = chatToolbar.findViewById(R.id.menu_button)
//            val titleTextView: TextView = chatToolbar.findViewById(R.id.toolbar_title)
//
//            // 버튼 클릭 리스너 설정
//            leftButton.setOnClickListener {
//                parentFragmentManager.popBackStack()
//            }
//            menuButton.setOnClickListener {
//                showPopupMenu(menuButton)
//            }
//
//            // 새로운 Toolbar를 액티비티에 추가
//            it.replaceToolbar(chatToolbar)
//        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.chat_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.alarmActiveBtn -> {
                    // Handle "알림끄기" action
                    true
                }
                R.id.issueReportBtn -> {
                    // Handle "신고하기" action
                    true
                }
                R.id.exitChatRoomBtn -> {
                    // Handle "채팅방 나가기" action
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
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
}