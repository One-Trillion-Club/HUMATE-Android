package com.otclub.humate.chat.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.otclub.humate.MainActivity
import com.otclub.humate.R
import com.otclub.humate.chat.adapter.ChatRoomAdapter
import com.otclub.humate.chat.viewModel.ChatRoomViewModel
import com.otclub.humate.databinding.ChatRoomFragmentBinding

class ChatRoomFragment  : Fragment()  {
    private val chatRoomViewModel: ChatRoomViewModel by activityViewModels()
    private var mBinding: ChatRoomFragmentBinding? = null

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
        mBinding?.tabLayout?.addTab(mBinding!!.tabLayout.newTab().setText(getString(R.string.chat_mate_list)))
        mBinding?.tabLayout?.addTab(mBinding!!.tabLayout.newTab().setText(getString(R.string.chat_pending_list)))
        mBinding?.tabLayout?.getTabAt(1)?.select()

        mBinding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        // "채팅방 리스트" 탭 선택 시
                        chatRoomViewModel.fetchChatRoomList("K_1")
                        //chatRoomViewModel.setSelectedButton(R.id.mateListButton)
                    }
                    1 -> {
                        // "대기 중인 채팅방 리스트" 탭 선택 시
                        chatRoomViewModel.fetchPendingChatRoomList("K_1")
                        //chatRoomViewModel.setSelectedButton(R.id.pendingListButton)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택되지 않은 경우 처리 (필요에 따라 추가)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택된 경우 처리 (필요에 따라 추가)
            }
        })



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
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}