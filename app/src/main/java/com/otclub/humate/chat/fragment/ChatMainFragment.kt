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
import com.otclub.humate.chat.adapter.RoomAdapter
import com.otclub.humate.chat.viewModel.ChatViewModel
import com.otclub.humate.databinding.ChatMainFragmentBinding

class ChatMainFragment  : Fragment()  {
    private val chatViewModel : ChatViewModel by activityViewModels()
    private var mBinding: ChatMainFragmentBinding? = null
    var selectedTab : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ChatMainFragmentBinding.inflate(inflater, container, false)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mBinding?.chatTabLayout?.addTab(mBinding!!.chatTabLayout.newTab().setText(getString(R.string.chat_mate_list)))
//        mBinding?.chatTabLayout?.addTab(mBinding!!.chatTabLayout.newTab().setText(getString(R.string.chat_pending_list)))

        selectedTab = chatViewModel.tabSelect.value ?: 0

        // TabLayout에 탭 추가
        mBinding?.chatTabLayout?.apply {
            addTab(newTab().setText(getString(R.string.chat_mate_list)))
            addTab(newTab().setText(getString(R.string.chat_pending_list)))
        }

        mBinding?.chatTabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        // "채팅방 리스트" 탭 선택 시
                        chatViewModel.setChatRoomList(emptyList())
                        chatViewModel.fetchChatRoomList()
                        chatViewModel.setTabSelect(tab?.position!!)
                        //chatRoomViewModel.setSelectedButton(R.id.mateListButton)
                    }
                    1 -> {
                        // "대기 중인 채팅방 리스트" 탭 선택 시
                        chatViewModel.setChatRoomList(emptyList())
                        chatViewModel.fetchPendingChatRoomList()
                        chatViewModel.setTabSelect(tab?.position!!)
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

//        mBinding?.chatTabLayout?.getTabAt(selectedTab)?.select()
        // 기본으로 첫 번째 탭 선택 및 API 호출
        if (selectedTab == 0) {  // 첫 번째 탭이 선택된 경우
            mBinding?.chatTabLayout?.getTabAt(0)?.select() // 첫 번째 탭 선택
            chatViewModel.fetchChatRoomList() // 첫 번째 탭에 해당하는 API 호출
        } else {  // 두 번째 탭이 선택된 경우
            mBinding?.chatTabLayout?.getTabAt(1)?.select() // 두 번째 탭 선택
            chatViewModel.fetchPendingChatRoomList() // 두 번째 탭에 해당하는 API 호출
        }

        // RecyclerView 설정
        mBinding?.chatRoomRecyclerView?.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = RoomAdapter(emptyList()) { chatRoom ->
                // 아이템 클릭 시 ChatFragment로 이동 -> 설정해줘야 함
                chatViewModel.setChatDetailDTO(chatRoom)
                findNavController().navigate(
                    R.id.action_chatMainFragment_to_chatFragment
                )
            }
        }

        // ViewModel에서 데이터 관찰
        chatViewModel.roomDetailDTOList.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = RoomAdapter(it) { chatRoom ->
                    // Navigation Bar 숨기기
                    (activity as? MainActivity)?.hideBottomNavigationBar()
                    chatViewModel.setChatDetailDTO(chatRoom)
                    findNavController().navigate(
                        R.id.action_chatMainFragment_to_chatFragment
                    )
                }
                Log.i("adapter : ", adapter.toString())
                mBinding?.chatRoomRecyclerView?.adapter = adapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //chatViewModel.setTabSelect(0)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}