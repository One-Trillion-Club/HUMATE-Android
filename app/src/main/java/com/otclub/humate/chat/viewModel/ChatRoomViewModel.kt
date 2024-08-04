package com.otclub.humate.chat.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.chat.api.ChatRoomService
import com.otclub.humate.chat.data.ChatRoomDetailDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.*

class ChatRoomViewModel : ViewModel() {
    private val chatRoomService : ChatRoomService = RetrofitConnection.getInstance().create(ChatRoomService::class.java)
    val chatRoomDetailDTOList = MutableLiveData<List<ChatRoomDetailDTO>>()

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
}