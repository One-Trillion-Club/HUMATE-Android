package com.otclub.humate.chat.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.chat.api.ChatService
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {
    private val chatService : ChatService = RetrofitConnection.getInstance().create(ChatService::class.java)
    val chatHistoryList = MutableLiveData<List<ChatMessageResponseDTO>>()

    // 채팅 메시지 리스트를 업데이트
    fun setChatHistory(newMessages: List<ChatMessageResponseDTO>) {
        chatHistoryList.value = newMessages
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
}