package com.otclub.humate.chat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.chat.api.ChatRoomService
import com.otclub.humate.chat.api.ChatService
import com.otclub.humate.chat.data.*
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

class ChatViewModel : ViewModel() {
    private val chatRoomService : ChatRoomService = RetrofitConnection.getInstance().create(ChatRoomService::class.java)
    private val chatService : ChatService = RetrofitConnection.getInstance().create(ChatService::class.java)
    val chatRoomDetailDTOList = MutableLiveData<List<ChatRoomDetailDTO>>()
    val chatHistoryList = MutableLiveData<List<ChatMessageResponseDTO>>()
    val latestChatRoomDetailDTO = MutableLiveData<ChatRoomDetailDTO>()
    val tabSelect = MutableLiveData<Int>()
    private val _navigateToChatFragment = MutableLiveData<Boolean>()
    val navigateToChatFragment: LiveData<Boolean> get() = _navigateToChatFragment

    private val _shouldOpenCompanionConfirmDialog = MutableLiveData<Boolean>()
    val shouldOpenCompanionConfirmDialog: LiveData<Boolean> get() = _shouldOpenCompanionConfirmDialog
    private val _shouldShowNotice = MutableLiveData<Boolean>()
    val shouldShowNotice: LiveData<Boolean> get() = _shouldShowNotice
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

    fun fetchChatRoomList()  {

        chatRoomService.getChatRoomList().enqueue(object :
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

    fun fetchPendingChatRoomList()  {

        chatRoomService.getPendingChatRoomList().enqueue(object :
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


    fun updateMateState(@Body requestDTO: MateUpdateRequestDTO){
        chatService.updateMateState(requestDTO).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: Response<CommonResponseDTO>
            ) {
                if (response.body()?.success == true) {
                    latestChatRoomDetailDTO.value?.let { currentDetail ->
                        latestChatRoomDetailDTO.value = currentDetail.copy(
                            isClicked = if (currentDetail.isClicked == 1) 0 else 1
                        )

                        Log.d("[bindChatDetails]", (requestDTO.isClicked == 1).toString() + (currentDetail.targetIsClicked == 1).toString() + (currentDetail.isMatched == 1).toString() )
                        if(requestDTO.isClicked == 1 && currentDetail.targetIsClicked == 1){
                            Log.d("[bindChatDetails]", (requestDTO.isClicked == 1).toString() + (currentDetail.targetIsClicked == 1).toString() + (currentDetail.isMatched == 1).toString() )
                            _shouldOpenCompanionConfirmDialog.value = true
                        }
                    }

                    Log.d("latestChatRoomDetailDTO : ", latestChatRoomDetailDTO.toString())
                    // 네비게이션 이벤트 발생
                    _navigateToChatFragment.value = true
                }
            }
            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {

            }

        })
    }

    fun companionStart(@Body requestDTO: ChatCreateCompanionRequestDTO){

        chatService.companionStart(requestDTO).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: Response<CommonResponseDTO>
            ) {
                Log.d("[shouldShowNotice 1]", response.body().toString())
                if (response.body()?.success == true) {
                    latestChatRoomDetailDTO.value?.let { currentDetail ->
                        latestChatRoomDetailDTO.value = currentDetail.copy(
                            isMatched = 1
                        )
                        Log.d("[shouldShowNotice 2]", (currentDetail?.isMatched == 1).toString() )
                        _shouldShowNotice.value = true
                    }
                }
                // 네비게이션 이벤트 발생
                _navigateToChatFragment.value = true
            }
            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {

            }
        })
    }

    fun resetNavigation() {
        _navigateToChatFragment.value = false
    }

    fun resetDialog() {
        _shouldOpenCompanionConfirmDialog.value = false
    }

    fun setChatDetailDTO(chatRoomDetailDTO: ChatRoomDetailDTO) {
        latestChatRoomDetailDTO.value = chatRoomDetailDTO
    }
}