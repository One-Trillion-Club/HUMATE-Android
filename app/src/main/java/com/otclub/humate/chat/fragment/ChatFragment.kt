package com.otclub.humate.chat.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.otclub.humate.BuildConfig.*
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.ChatAdapter
import com.otclub.humate.chat.data.*
import com.otclub.humate.chat.viewModel.ChatViewModel
import com.otclub.humate.databinding.ChatFragmentBinding
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class ChatFragment : Fragment() {
    private lateinit var sharedPreferencesManager : SharedPreferencesManager
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

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        // RecyclerView 설정
        chatAdapter = ChatAdapter(mutableListOf(), chatRoomDetailDTO?.participateId) //
        mBinding?.chatDisplay?.adapter = chatAdapter
        mBinding?.chatDisplay?.layoutManager = LinearLayoutManager(requireContext())
        scrollToBottom()

//        // ViewModel의 상태를 관찰합니다.
//        Log.d("[bindChatDetails]", (chatRoomDetailDTO?.isClicked == 1).toString() + (chatRoomDetailDTO?.targetIsClicked == 1).toString() + (chatRoomDetailDTO?.isMatched == 1).toString() )
//        if(chatRoomDetailDTO!=null && chatRoomDetailDTO?.isClicked == 1 && chatRoomDetailDTO?.targetIsClicked == 1 && chatRoomDetailDTO?.isMatched  == 0) {
//            showMateDialogConfirmed()
//        }




        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        bindChatDetails()
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

        // ViewModel의 네비게이션 이벤트를 관찰
        chatViewModel.navigateToChatFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(R.id.action_chatFragment_to_chatFragment)
                // 이벤트 처리 후, LiveData 값을 초기화
                chatViewModel.resetNavigation()
            }
        }

        // 동행 Open Dialog
        chatViewModel.shouldOpenCompanionConfirmDialog.observe(viewLifecycleOwner) { dialog ->
            if (dialog) {
                Log.d("[bindChatDetails]", (chatRoomDetailDTO?.isClicked == 1).toString() + (chatRoomDetailDTO?.targetIsClicked == 1).toString() + (chatRoomDetailDTO?.isMatched == 1).toString() )
                showMateDialogConfirmed()
                chatViewModel.resetDialog()
            }
        }

        // 동행 Open Dialog
        chatViewModel.shouldShowNotice.observe(viewLifecycleOwner) { notice ->
            if (notice) {
                Log.d("[shouldShowNotice]", (chatRoomDetailDTO?.isMatched == 1).toString() )
                updateNoticeVisibility()
                chatViewModel.setTabSelect(0)
            }
        }

        // 비동기적으로 과거 채팅 내역 로드 및 웹소켓 시작
        lifecycleScope.launch {
            loadChatHistory() // 과거 채팅 내역 로드
            handler.post(reconnectRunnable) // 웹소켓 시작
        }
    }

    // 채팅 관련 메서드
    private suspend fun loadChatHistory() {
        withContext(Dispatchers.IO) {
            chatViewModel.fetchChatHistoryList(chatRoomDetailDTO?.chatRoomId.toString())
            Log.d("[loadChatHistory]", chatRoomDetailDTO?.chatRoomId.toString())
        }
    }

    private fun sendMessage(content: String) {
        val messageRequest = ChatMessageRequestDTO(
            chatRoomId = chatRoomDetailDTO?.chatRoomId.toString(),
            participateId = chatRoomDetailDTO?.participateId, // 실제 사용자 ID로 변경 //
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

    // 웹 소켓 관련 메서드
    private fun startWebSocket(){
        val (ajt, rjt) = sharedPreferencesManager.getLoginToken()

        client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .header("Authorization", chatRoomDetailDTO?.participateId.toString())
            .addHeader("Cookie", "ajt=$ajt; rjt=$rjt")
            .build()

        webSocketListener = ChatWebSocketListener(this)
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private fun closeWebSocket() {
        webSocket?.close(1000, "[closeWebSocket] - Fragment is pausing")
        webSocket = null
    }


    private fun bindChatDetails() {
        mBinding?.postTitle?.text = chatRoomDetailDTO?.postTitle.toString()
        mBinding?.matchDate?.text = chatRoomDetailDTO?.matchDate.toString()
        mBinding?.matchBranch?.text = chatRoomDetailDTO?.matchBranch.toString()
        mBinding?.mateButton?.setOnClickListener {
            showPopupMateUpdate()
        }

        // 두 값이 null일 수 있으므로 null 체크를 수행하고, null일 경우 0으로 처리합니다.
        val isClickedValue = chatRoomDetailDTO?.isClicked ?: 0
        val targetIsClickedValue = chatRoomDetailDTO?.targetIsClicked ?: 0

        // 버튼 색상 설정
        val buttonResource = if (isClickedValue == 1) {
            R.drawable.chat_mate_button_pressed  // 클릭된 상태의 배경
        } else {
            R.drawable.chat_mate_button_default  // 기본 상태의 배경
        }
        mBinding?.mateButton?.setBackgroundResource(buttonResource)

        // 아이콘 색상 설정
        val iconResource = if (isClickedValue == 1) {
            R.drawable.ic_logo_purple  // 클릭된 상태의 아이콘 색상 (보라색)
        } else {
            R.drawable.ic_logo_gray  // 기본 상태의 아이콘 색상 (회색)
        }
        val drawable = ContextCompat.getDrawable(requireContext(), iconResource)

        // 텍스트 색상 설정
        val textColorResource = if (isClickedValue == 1) {
            R.color.humate_main  // 클릭된 상태의 텍스트 색상
        } else {
            R.color.bright_gray  // 기본 상태의 텍스트 색상
        }
        val textColor = ContextCompat.getColor(requireContext(), textColorResource)
        mBinding?.mateButton?.setTextColor(textColor)

        mBinding?.mateButton?.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        Log.d("[Click]", isClickedValue.toString() + " " + targetIsClickedValue.toString())
        // 두 값을 더한 후 String으로 변환합니다.
        mBinding?.mateButton?.text = (isClickedValue + targetIsClickedValue).toString()

    }

    private fun showPopupMateUpdate() {
        // 팝업 창 레이아웃 선택
        val layoutResId = if (chatRoomDetailDTO?.isClicked == 1) {
            R.layout.chat_dialog_mate_cancel
        } else {
            R.layout.chat_dialog_mate
        }

        // 팝업 창
        val dialogView = LayoutInflater.from(context).inflate(layoutResId, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val cancelButton: Button = dialogView.findViewById(R.id.dialog_cancel_button)
        val confirmButton: Button = dialogView.findViewById(R.id.dialog_confirm_button)

        val dialog = dialogBuilder.create()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            updateMate()
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showMateDialogConfirmed() {
        // 팝업 창
        val dialogView = LayoutInflater.from(context).inflate(R.layout.chat_dialog_confirmed, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val confirmButton: Button = dialogView.findViewById(R.id.dialog_confirm_button)
        val dialog = dialogBuilder.create()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val requestDTO = ChatCreateCompanionRequestDTO(
            chatRoomId = chatRoomDetailDTO?.chatRoomId
        )

        confirmButton.setOnClickListener {
            chatViewModel.companionStart(requestDTO)
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
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

    private fun scrollToBottom() {
        mBinding?.chatDisplay?.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun updateMate() {
        val requestDTO = MateUpdateRequestDTO(
            chatRoomId = chatRoomDetailDTO?.chatRoomId,
            participateId = chatRoomDetailDTO?.participateId,
            isClicked = if (chatRoomDetailDTO?.isClicked == 1) {
                0
            } else {
                1
            }
        )
        chatViewModel.updateMateState(requestDTO)
    }


    private fun updateNoticeVisibility() {
        val chatActivityNoticeContainer = mBinding?.chatActivityNoticeContainer

        if (chatRoomDetailDTO?.isMatched == 1) {
            chatActivityNoticeContainer?.visibility = View.VISIBLE
        } else {
            chatActivityNoticeContainer?.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("[onResume]","다시 시작")
        Log.d("[onResume]", chatRoomDetailDTO?.isMatched.toString())
        if(chatRoomDetailDTO!=null && chatRoomDetailDTO?.isMatched==1)
            updateNoticeVisibility()
    }

    override fun onPause() {
        super.onPause()
        Log.d("[onPause]","일시정지")
        handler.removeCallbacks(reconnectRunnable)
        closeWebSocket()
    }

    override fun onDestroyView() {
        mBinding = null

        super.onDestroyView()
    }

}