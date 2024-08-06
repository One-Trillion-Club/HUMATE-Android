package com.otclub.humate.chat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.chat.api.ChatRoomService
import com.otclub.humate.chat.api.ChatService
import com.otclub.humate.chat.data.*
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {
    private val chatRoomService : ChatRoomService = RetrofitConnection.getInstance().create(ChatRoomService::class.java)
    private val chatService : ChatService = RetrofitConnection.getInstance().create(ChatService::class.java)
    val chatRoomDetailDTOList = MutableLiveData<List<ChatRoomDetailDTO>>()
    val chatHistoryList = MutableLiveData<List<ChatMessageResponseDTO>>()
    val latestChatRoomDetailDTO = MutableLiveData<ChatRoomDetailDTO>()
    val tabSelect = MutableLiveData<Int>()

    fun setTabSelect(tab: Int) {
        tabSelect.value = tab
    }

    // 채팅 메시지 리스트를 업데이트
    fun setChatRoomList(roomList: List<ChatRoomDetailDTO>) {
        chatRoomDetailDTOList.value = roomList
    }

    fun fetchChatHistoryList(chatRoomId: String?)  {

        chatService.getChatHistoryList(chatRoomId!!).enqueue(object :
            Callback<List<ChatMessageResponseDTO>> {
            override fun onResponse(
                call: Call<List<ChatMessageResponseDTO>>,
                response: Response<List<ChatMessageResponseDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("chatHistoryList : ", response.body().toString())
                    chatHistoryList.value = response.body();
                }
            }

            override fun onFailure(call: Call<List<ChatMessageResponseDTO>>, t: Throwable) {
                Log.e("채팅 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }

    fun fetchChatRoomList(memberId: String)  {

        chatRoomService.getChatRoomList(memberId!!).enqueue(object :
            Callback<List<ChatRoomDetailDTO>> {
            override fun onResponse(
                call: Call<List<ChatRoomDetailDTO>>,
                response: Response<List<ChatRoomDetailDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("chatRoomListResponse : ", response.body().toString())
                    chatRoomDetailDTOList.value = response.body();
                }
            }

            override fun onFailure(call: Call<List<ChatRoomDetailDTO>>, t: Throwable) {
                Log.e("채팅 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }

    fun fetchPendingChatRoomList(memberId: String)  {

        chatRoomService.getPendingChatRoomList(memberId!!).enqueue(object :
            Callback<List<ChatRoomDetailDTO>> {
            override fun onResponse(
                call: Call<List<ChatRoomDetailDTO>>,
                response: Response<List<ChatRoomDetailDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("chatRoomListResponse : ", response.body().toString())
                    chatRoomDetailDTOList.value = response.body();
                }
            }

            override fun onFailure(call: Call<List<ChatRoomDetailDTO>>, t: Throwable) {
                Log.e("채팅 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }


    fun setChatDetailDTO(chatRoomDetailDTO: ChatRoomDetailDTO) {
        latestChatRoomDetailDTO.value = chatRoomDetailDTO
    }
}