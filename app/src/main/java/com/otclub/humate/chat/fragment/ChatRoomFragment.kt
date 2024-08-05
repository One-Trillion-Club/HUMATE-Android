package com.otclub.humate.chat.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.ChatRoomAdapter
import com.otclub.humate.chat.viewModel.ChatRoomViewModel
import com.otclub.humate.databinding.ChatRoomFragmentBinding

class ChatRoomFragment  : Fragment()  {
    private val chatRoomViewModel: ChatRoomViewModel by activityViewModels()
    private var mBinding: ChatRoomFragmentBinding? = null
    private var selectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ChatRoomFragmentBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        mBinding?.chatRoomRecyclerView?.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = ChatRoomAdapter(emptyList()) { chatRoom ->
                // 아이템 클릭 시 ChatFragment로 이동 -> 설정해줘야 함
                findNavController().navigate(
                    R.id.action_chatRoomFragment_to_chatFragment,
                    Bundle().apply { putInt("participateId", chatRoom.participateId) }
                )
            }
        }

        // ViewModel에서 데이터 관찰
        chatRoomViewModel.chatRoomDetailDTOList.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = ChatRoomAdapter(it) { chatRoom ->
                    val bundle = Bundle().apply {
                        putString("participateId", chatRoom.participateId.toString())
                        putString("chatRoomId", chatRoom.chatRoomId)
                    }
                    // Navigation Bar 숨기기
                    (activity as? MainActivity)?.hideBottomNavigationBar()

                    findNavController().navigate(
                        R.id.action_chatRoomFragment_to_chatFragment,
                        bundle
                    )
                }
                Log.i("adapter : ", adapter.toString())
                mBinding?.chatRoomRecyclerView?.adapter = adapter
            }
        }

        // 버튼 클릭 리스너 설정
        mBinding?.mateListButton?.setOnClickListener {
            selectButton(mBinding?.mateListButton)
            chatRoomViewModel.fetchChatRoomList("K_1")
        }

        mBinding?.pendingListButton?.setOnClickListener {
            selectButton(mBinding?.pendingListButton)
            chatRoomViewModel.fetchPendingChatRoomList("K_1")
        }

        selectButton(mBinding?.mateListButton)
        chatRoomViewModel.fetchChatRoomList("K_1")
    }

    private fun selectButton(button: Button?) {
        // 기존 선택된 버튼이 있다면 원래 상태로 되돌리기
        selectedButton?.let {
            it.isSelected = false // 선택 상태를 해제
            it.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            it.setTextColor(Color.BLACK)
            it.setBackgroundResource(R.drawable.tab_basic_indicator) // 버튼 배경을 설정
        }

        // 새로 선택된 버튼의 스타일 적용
        button?.let {
            it.isSelected = true
            it.setTypeface(Typeface.DEFAULT_BOLD) // 텍스트를 굵게
            it.setTextColor(Color.BLACK) // 텍스트 색깔을 검은색으로 설정
            it.setBackgroundResource(R.drawable.tab_indicator) // 버튼 배경을 설정
        }

        selectedButton = button
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}