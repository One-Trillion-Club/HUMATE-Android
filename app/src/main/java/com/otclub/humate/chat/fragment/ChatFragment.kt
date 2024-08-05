package com.otclub.humate.chat.fragment

import android.annotation.SuppressLint
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.otclub.humate.BuildConfig.*
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.ChatAdapter
import com.otclub.humate.chat.data.ChatMessageRequestDTO
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import com.otclub.humate.chat.data.MessageType
import com.otclub.humate.chat.viewModel.ChatViewModel
import com.otclub.humate.databinding.ChatFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import java.util.concurrent.TimeUnit

class ChatFragment : Fragment() {

    private var participateId: String? = null
    private var chatRoomId : String? = null
    private val chatViewModel : ChatViewModel by activityViewModels()
    private var mBinding : ChatFragmentBinding? = null
    private lateinit var webSocketListener: ChatWebSocketListener
    private lateinit var client: OkHttpClient
    private var webSocket : WebSocket ?= null
    private val handler = Handler(Looper.getMainLooper())
    private val reconnectRunnable = object : Runnable {
        override fun run() {
            if (webSocket == null) {
                Log.d("WebSocket", "웹소켓 재연결 요청")
                closeWebSocket()
                startWebSocket()
            }
            handler.postDelayed(this, 180000) // 3분마다 재연결 요청
        }
    }

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = ChatFragmentBinding.inflate(inflater, container, false)
        mBinding = binding

        // Get participateId from arguments
        arguments?.let {
            participateId = it.getString("participateId", "")
            chatRoomId = it.getString("chatRoomId", "")

            // Use participateId as needed
            Log.d("ChatFragment", "Received participateId: $participateId")
        }
        // RecyclerView 설정
        chatAdapter = ChatAdapter(mutableListOf(), participateId)
        mBinding?.chatDisplay?.adapter = chatAdapter
        mBinding?.chatDisplay?.layoutManager = LinearLayoutManager(requireContext())
        scrollToBottom()

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSendButton()

        // ViewModel에서 데이터 관찰
        chatViewModel.chatHistoryList.observe(viewLifecycleOwner) { response ->
            response?.let {
                chatAdapter.updateMessages(it)
                Log.i("adapter : ", it.toString())
                mBinding?.chatDisplay?.adapter = chatAdapter
                scrollToBottom()
            }
        }

        // 비동기적으로 과거 채팅 내역 로드 및 웹소켓 시작
        lifecycleScope.launch {
            loadChatHistory() // 과거 채팅 내역 로드
            handler.post(reconnectRunnable) // 웹소켓 시작
        }
    }

    private suspend fun loadChatHistory() {
        withContext(Dispatchers.IO) {
            chatViewModel.fetchChatHistoryList(chatRoomId)
            Log.d("[loadChatHistory]", chatRoomId.toString())

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("[onResume]","다시 시작")
    }

    override fun onPause() {
        super.onPause()
        Log.d("[onPause]","일시정지")
        handler.removeCallbacks(reconnectRunnable)
        closeWebSocket()
    }

    private fun sendMessage(content: String) {
        val messageRequest = ChatMessageRequestDTO(
            chatRoomId = chatRoomId,
            participateId = participateId, // 실제 사용자 ID로 변경
            content = content,
            messageType = MessageType.TEXT
        )

        val gson = Gson()
        val messageJson = gson.toJson(messageRequest)
        webSocket?.send(messageJson)
    }

    fun updateChat(message: ChatMessageResponseDTO) {
        chatAdapter.addMessage(message)
        scrollToBottom()
    }

    private fun startWebSocket(){

        client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .header("Authorization", participateId.toString())
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

    private fun scrollToBottom() {
        mBinding?.chatDisplay?.scrollToPosition(chatAdapter.itemCount - 1)
    }

    @SuppressLint("ResourceType")
    private fun setupToolbar() {
        // 새로운 Toolbar 설정
        val chatToolbar = LayoutInflater.from(context).inflate(R.layout.chat_toolbar, null) as Toolbar
        val leftButton: ImageButton = chatToolbar.findViewById(R.id.left_button)
        val menuButton: ImageButton = chatToolbar.findViewById(R.id.menu_button)
        val titleTextView: TextView = chatToolbar.findViewById(R.id.toolbar_title)

        // 버튼 클릭 리스너 설정
        leftButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        menuButton.setOnClickListener {
            showPopupMenu(menuButton)
        }

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