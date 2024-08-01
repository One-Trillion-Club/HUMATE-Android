package com.otclub.humate.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.otclub.humate.BuildConfig.TEST_MEMBER_2
import com.otclub.humate.BuildConfig.WEBSOCKET_URL
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.databinding.FragmentChatBinding
import java.net.URI

class ChatFragment : Fragment() {

    private var mBinding : FragmentChatBinding? = null
    private lateinit var webSocketClient: ChatWebSocketClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentChatBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val serverUri = URI(WEBSOCKET_URL)
        Log.d("[ChatFragment]","ServerURI : ${WEBSOCKET_URL}")

        // 헤더 설정
        val headers = mapOf(
            "authorization" to TEST_MEMBER_2
        )

        webSocketClient = ChatWebSocketClient(serverUri, headers)
        webSocketClient.connect()

        mBinding?.sendButton?.setOnClickListener {
            val message = mBinding?.messageInput?.text.toString()
            if (message.isNotEmpty()) {
                webSocketClient.send(message)
                mBinding?.chatDisplay?.append("\nMe: $message")
                mBinding?.messageInput?.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}