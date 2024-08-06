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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.otclub.humate.BuildConfig.*
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.ChatAdapter
import com.otclub.humate.chat.data.ChatMessageRequestDTO
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import com.otclub.humate.chat.data.ChatRoomDetailDTO
import com.otclub.humate.chat.data.MessageType
import com.otclub.humate.chat.viewModel.ChatViewModel
import com.otclub.humate.common.LoadingDialog
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
    private var chatRoomDetailDTO: ChatRoomDetailDTO? = null
    private val chatViewModel : ChatViewModel by activityViewModels()
    private var mBinding : ChatFragmentBinding? = null
    private lateinit var webSocketListener: ChatWebSocketListener
    private lateinit var client: OkHttpClient
    private lateinit var chatAdapter: ChatAdapter
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        val binding = ChatFragmentBinding.inflate(inflater, container, false)
        mBinding = binding

        val currentDetail = chatViewModel.latestChatRoomDetailDTO.value
        chatRoomDetailDTO = currentDetail

        // RecyclerView 설정
        chatAdapter = ChatAdapter(mutableListOf(), chatRoomDetailDTO?.participateId.toString())
        mBinding?.chatDisplay?.adapter = chatAdapter
        mBinding?.chatDisplay?.layoutManager = LinearLayoutManager(requireContext())
        scrollToBottom()



        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        bindChatDetails()
        setupSendButton()

        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()

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
            loadingDialog.dismiss()
        }
    }

    private suspend fun loadChatHistory() {
        withContext(Dispatchers.IO) {
            chatViewModel.fetchChatHistoryList(chatRoomDetailDTO?.chatRoomId.toString())
            Log.d("[loadChatHistory]", chatRoomDetailDTO?.chatRoomId.toString())
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
            chatRoomId = chatRoomDetailDTO?.chatRoomId.toString(),
            participateId = chatRoomDetailDTO?.participateId.toString(), // 실제 사용자 ID로 변경
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
            .header("Authorization", chatRoomDetailDTO?.participateId.toString())
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

    private fun scrollToBottom() {
        mBinding?.chatDisplay?.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun setupToolbar() {
        val toolbar = mBinding?.toolbar?.chatToolbar
        toolbar?.let {
            val leftButton: ImageButton = it.findViewById(R.id.left_button)
            val menuButton: ImageButton = it.findViewById(R.id.menu_button)
            val title: TextView = it.findViewById(R.id.toolbar_title)
            title.text = chatRoomDetailDTO?.targetNickname.toString()

            // 버튼의 가시성 설정
            val showLeftButton = true
            val showRightButton = true
            leftButton.visibility = if (showLeftButton) View.VISIBLE else View.GONE
            menuButton.visibility = if (showRightButton) View.VISIBLE else View.GONE

            // leftButton 클릭 시 이전 화면으로 돌아가기
            leftButton.setOnClickListener {
//                val selectedTab = (activity as MainActivity).findViewById<TabLayout>(R.id.chatTabLayout).selectedTabPosition
//                chatViewModel.setTabSelect(selectedTab)
                findNavController().navigateUp()
                (activity as? MainActivity)?.showBottomNavigationBar()
            }

            menuButton.setOnClickListener {
                showPopupMenu(menuButton)
            }
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

    private fun bindChatDetails() {
        mBinding?.postTitle?.text = chatRoomDetailDTO?.postTitle.toString()
        mBinding?.matchDate?.text = chatRoomDetailDTO?.matchDate.toString()
        mBinding?.matchBranch?.text = chatRoomDetailDTO?.matchBranch.toString()

        // 두 값이 null일 수 있으므로 null 체크를 수행하고, null일 경우 0으로 처리합니다.
        val isClickedValue = chatRoomDetailDTO?.isClicked ?: 0
        val targetIsClickedValue = chatRoomDetailDTO?.targetIsClicked ?: 0

        // 두 값을 더한 후 String으로 변환합니다.
        mBinding?.matchButtonText?.text = (isClickedValue + targetIsClickedValue).toString()
    }
}