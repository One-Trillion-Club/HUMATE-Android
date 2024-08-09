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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.otclub.humate.BuildConfig.WEBSOCKET_URL
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.MessageAdapter
import com.otclub.humate.chat.data.*
import com.otclub.humate.chat.viewModel.ChatViewModel
import com.otclub.humate.common.LoadingDialog
import com.otclub.humate.databinding.ChatFragmentBinding
import com.otclub.humate.member.viewmodel.MemberViewModel
import com.otclub.humate.sharedpreferences.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

/**
 * 1:1 채팅 Fragment
 * @author 최유경
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   손승완        최초 생성
 * 2024.08.01   최유경        웹소켓 연결
 * 2024.08.02   최유경        메세지 전송
 * 2024.08.04   최유경        과거 채팅 내역 조회, 화면 구성
 * 2024.08.05   최유경        채팅 툴바
 * 2024.08.07   최유경        채팅 상단 매칭글 정보 구성, 메이트 맺기 버튼, 다이얼 기능 추가
 * </pre>
 */
class ChatFragment : Fragment() {
    private lateinit var sharedPreferencesManager : SharedPreferencesManager
    private var roomDetailDTO: RoomDetailDTO? = null
    private lateinit var messageAdapter: MessageAdapter
    private val chatViewModel : ChatViewModel by activityViewModels()
    private val memberViewModel: MemberViewModel by activityViewModels()
    private var mBinding : ChatFragmentBinding? = null

    private lateinit var webSocketListener: ChatWebSocketListener
    private lateinit var client: OkHttpClient
    private var webSocket : WebSocket ?= null
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 웹소켓 재연결 Runnable
     */
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

    /**
     * 1:1 채팅방 View 생성
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        val binding = ChatFragmentBinding.inflate(inflater, container, false)
        mBinding = binding


        val currentDetail = chatViewModel.latestRoomDetailDTO.value
        roomDetailDTO = currentDetail


        // RecyclerView 설정
        messageAdapter = MessageAdapter(mutableListOf(), roomDetailDTO, onMateClick = { memberId ->
            // 카드 뷰 클릭 시 모달 창 띄우기
            memberViewModel.getOtherMemberProfile(
                memberId = memberId,
                onSuccess = { profile ->
                    val loadingDialog = LoadingDialog(requireContext())
                    loadingDialog.showMateDetailPopup(profile)
                },
                onError = { error ->
                    Toast.makeText(context, R.string.toast_please_one_more_time, Toast.LENGTH_SHORT).show()
                }
            )
        })

        // RecyclerView 설정
        mBinding?.chatRecyclerView?.adapter = messageAdapter
        mBinding?.chatRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        scrollToBottom()

        return mBinding?.root
    }

    /**
     * View 생성 후 화면 구성
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar() // 툴바 설정
        bindChatDetails() // 채팅방 info 설정
        setupSendButton() // 전송 버튼 설정

        // ViewModel에서 데이터 관찰
        chatViewModel.chatHistoryList.observe(viewLifecycleOwner) { response ->
            response?.let {
                Log.d("chatHistoryList.observe",it.toString())
                messageAdapter.updateMessages(it, roomDetailDTO)
                mBinding?.chatRecyclerView?.adapter = messageAdapter
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
                Log.d("[bindChatDetails]", (roomDetailDTO?.isClicked == 1).toString() + (roomDetailDTO?.targetIsClicked == 1).toString() + (roomDetailDTO?.isMatched == 1).toString() )
                showMateDialogConfirmed()
                chatViewModel.resetDialog()
            }
        }

//        // 동행 Open Dialog
//        chatViewModel.shouldShowNotice.observe(viewLifecycleOwner) { notice ->
//            if (notice) {
//                Log.d("[shouldShowNotice]", (chatRoomDetailDTO?.isMatched == 1).toString() )
//                updateNoticeVisibility()
//                chatViewModel.setTabSelect(0)
//            }
//        }

        // 비동기적으로 과거 채팅 내역 로드 및 웹소켓 시작
        lifecycleScope.launch {
            loadChatHistory() // 과거 채팅 내역 로드
            handler.post(reconnectRunnable) // 웹소켓 시작
        }
    }

    /**
     * 채팅 과거 내역 조회 화면 구성
     */
    private suspend fun loadChatHistory() {
        withContext(Dispatchers.IO) {
            chatViewModel.fetchChatHistoryList(roomDetailDTO?.chatRoomId.toString())
            Log.d("[loadChatHistory]", roomDetailDTO?.chatRoomId.toString())
        }
    }

    /**
     * 웹 소켓 시작하는 메서드
     */
    private fun startWebSocket(){
        val (ajt, rjt) = sharedPreferencesManager.getLoginToken()

        client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .header("Authorization", roomDetailDTO?.participateId.toString())
            .addHeader("Cookie", "ajt=$ajt; rjt=$rjt")
            .build()

        webSocketListener = ChatWebSocketListener(this)
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    /**
     * 웹소켓 연결 해제 메서드
     */
    private fun closeWebSocket() {
        webSocket?.close(1000, "[closeWebSocket] - Fragment is pausing")
        webSocket = null
    }

    /**
     * 매칭글 정보 배치
     */
    private fun bindChatDetails() {
        mBinding?.postTitle?.text = roomDetailDTO?.postTitle.toString()
        mBinding?.matchDate?.text = roomDetailDTO?.matchDate.toString()
        mBinding?.matchBranch?.text = roomDetailDTO?.matchBranch.toString()
        mBinding?.mateButton?.setOnClickListener {
            showPopupMateUpdate()
        }

        // 두 값이 null일 수 있으므로 null 체크를 수행하고, null일 경우 0으로 처리합니다.
        val isClickedValue = roomDetailDTO?.isClicked ?: 0
        val targetIsClickedValue = roomDetailDTO?.targetIsClicked ?: 0

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

    /**
     * 메이트 맺기 버튼 클릭 시 보여지는 다이얼
     */
    private fun showPopupMateUpdate() {
        // 팝업 창 레이아웃 선택
        val layoutResId = if (roomDetailDTO?.isClicked == 1) {
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

    /**
     * 쌍방 메이트 맺기 클릭 후 보여지는 메이트 확정 다이얼
     */
    private fun showMateDialogConfirmed() {
        // 팝업 창
        val dialogView = LayoutInflater.from(context).inflate(R.layout.chat_dialog_confirmed, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val confirmButton: Button = dialogView.findViewById(R.id.dialog_confirm_button)
        val dialog = dialogBuilder.create()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val requestDTO = CompanionCreateRequestDTO(
            chatRoomId = roomDetailDTO?.chatRoomId
        )

        confirmButton.setOnClickListener {
            chatViewModel.companionStart(requestDTO)
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
    }

    /**
     * 1:1 채팅방 toolbar 설정
     */
    private fun setupToolbar() {
        val toolbar = mBinding?.toolbar?.chatToolbar
        toolbar?.let {
            val leftButton: ImageButton = it.findViewById(R.id.left_button)
            val menuButton: ImageButton = it.findViewById(R.id.menu_button)
            val title: TextView = it.findViewById(R.id.toolbar_title)
            title.text = roomDetailDTO?.targetNickname.toString()

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

    /**
     * 상단 점 3개 버튼 클릭 시 보여지는 menu bar
     */
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

    /**
     * 메세지 버튼 전송 onClick 설정
     */
    private fun setupSendButton() {
        mBinding?.sendButton?.setOnClickListener {
            val message = mBinding?.messageInput?.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                mBinding?.messageInput?.text?.clear()
            }
        }
    }

    /**
     * 메세지 전송 메서드
     */
    private fun sendMessage(content: String) {
        val messageRequest = MessageRequestDTO(
            chatRoomId = roomDetailDTO?.chatRoomId.toString(),
            participateId = roomDetailDTO?.participateId,
            content = content,
            messageType = MessageType.TEXT
        )

        val gson = Gson()
        val messageJson = gson.toJson(messageRequest)
        webSocket?.send(messageJson)
    }

    /**
     * 채팅 view에 새로운 메세지 추가
     */
    fun updateChat(message: MessageWebSocketResponseDTO) { // 고쳐야함
        messageAdapter.addMessage(message)
        scrollToBottom()
    }

    /**
     * 채팅 기본값으로 하단 보여주는 메서드
     */
    private fun scrollToBottom() {
        mBinding?.chatRecyclerView?.scrollToPosition(messageAdapter.itemCount - 1)
    }

    /**
     * 메이트 맺기 버튼 클릭 시 동작
     */
    private fun updateMate() {
        val requestDTO = MateUpdateRequestDTO(
            chatRoomId = roomDetailDTO?.chatRoomId,
            participateId = roomDetailDTO?.participateId,
            isClicked = if (roomDetailDTO?.isClicked == 1) {
                0
            } else {
                1
            }
        )
        chatViewModel.updateMateState(requestDTO)
    }


    /**
     * 활동 공지 활성화/비활성화 처리
     */
    private fun updateNoticeVisibility() {
        val chatActivityNoticeContainer = mBinding?.chatActivityNoticeContainer

        if (roomDetailDTO?.isMatched == 1) {
            chatActivityNoticeContainer?.visibility = View.VISIBLE
        } else {
            chatActivityNoticeContainer?.visibility = View.GONE
        }
    }

    /**
     * ChatFragment 시작
     */
    override fun onResume() {
        super.onResume()
        Log.d("[onResume]","다시 시작")
        Log.d("[onResume]", roomDetailDTO?.isMatched.toString())
        if(roomDetailDTO!=null && roomDetailDTO?.isMatched==1)
            updateNoticeVisibility()
    }

    /**
     * ChatFragment 일시정지
     */
    override fun onPause() {
        super.onPause()
        Log.d("[onPause]","일시정지")
        handler.removeCallbacks(reconnectRunnable)
        closeWebSocket()
    }

    /**
     * ChatFragment 종료
     */
    override fun onDestroyView() {
        mBinding = null

        super.onDestroyView()
    }

}