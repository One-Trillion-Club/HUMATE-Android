package com.otclub.humate.chat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otclub.humate.chat.api.ChatMainService
import com.otclub.humate.chat.api.ChatService
import com.otclub.humate.chat.data.*
import com.otclub.humate.mission.data.CommonResponseDTO
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

class ChatViewModel : ViewModel() {
    private val chatMainService : ChatMainService = RetrofitConnection.getInstance().create(ChatMainService::class.java)
    private val chatService : ChatService = RetrofitConnection.getInstance().create(ChatService::class.java)
    val roomDetailDTOList = MutableLiveData<List<RoomDetailDTO>>()
    val chatHistoryList = MutableLiveData<List<Message>>()
    val latestRoomDetailDTO = MutableLiveData<RoomDetailDTO>()
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
    fun setChatRoomList(roomList: List<RoomDetailDTO>) {
        roomDetailDTOList.value = roomList
    }

    // 채팅 과거 내역 불러오는 API 호출
    fun fetchChatHistoryList(chatRoomId: String?)  {

        chatService.getChatHistoryList(chatRoomId!!).enqueue(object :
            Callback<List<Message>> {
            override fun onResponse(
                call: Call<List<Message>>,
                response: Response<List<Message>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("chatHistoryList : ", response.body().toString())
                    chatHistoryList.value = response.body();
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.e("채팅 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }

    fun fetchChatRoomList()  {

        chatMainService.getChatRoomList().enqueue(object :
            Callback<List<RoomDetailDTO>> {
            override fun onResponse(
                call: Call<List<RoomDetailDTO>>,
                response: Response<List<RoomDetailDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("chatRoomListResponse : ", response.body().toString())
                    roomDetailDTOList.value = response.body();
                }
            }

            override fun onFailure(call: Call<List<RoomDetailDTO>>, t: Throwable) {
                Log.e("채팅 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }

    fun fetchPendingChatRoomList()  {

        chatMainService.getPendingChatRoomList().enqueue(object :
            Callback<List<RoomDetailDTO>> {
            override fun onResponse(
                call: Call<List<RoomDetailDTO>>,
                response: Response<List<RoomDetailDTO>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("chatRoomListResponse : ", response.body().toString())
                    roomDetailDTOList.value = response.body();
                }
            }

            override fun onFailure(call: Call<List<RoomDetailDTO>>, t: Throwable) {
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
                    latestRoomDetailDTO.value?.let { currentDetail ->
                        latestRoomDetailDTO.value = currentDetail.copy(
                            isClicked = if (currentDetail.isClicked == 1) 0 else 1
                        )

                        Log.d("[bindChatDetails]", (requestDTO.isClicked == 1).toString() + (currentDetail.targetIsClicked == 1).toString() + (currentDetail.isMatched == 1).toString() )
                        if(requestDTO.isClicked == 1 && currentDetail.targetIsClicked == 1){
                            Log.d("[bindChatDetails]", (requestDTO.isClicked == 1).toString() + (currentDetail.targetIsClicked == 1).toString() + (currentDetail.isMatched == 1).toString() )
                            _shouldOpenCompanionConfirmDialog.value = true
                        }
                    }

                    Log.d("latestChatRoomDetailDTO : ", latestRoomDetailDTO.toString())
                    // 네비게이션 이벤트 발생
                    _navigateToChatFragment.value = true
                }
            }
            override fun onFailure(call: Call<CommonResponseDTO>, t: Throwable) {

            }

        })
    }

    fun companionStart(@Body requestDTO: CompanionCreateRequestDTO){

        chatService.companionStart(requestDTO).enqueue(object : Callback<CommonResponseDTO> {
            override fun onResponse(
                call: Call<CommonResponseDTO>,
                response: Response<CommonResponseDTO>
            ) {
                Log.d("[shouldShowNotice 1]", response.body().toString())
                if (response.body()?.success == true) {
                    latestRoomDetailDTO.value?.let { currentDetail ->
                        latestRoomDetailDTO.value = currentDetail.copy(
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


    fun createChatRoom(@Body requestDTO: RoomCreateRequestDTO){
        chatMainService.createChatRoom(requestDTO).enqueue(object : Callback<RoomCreateResponseDTO> {
            override fun onResponse(
                call: Call<RoomCreateResponseDTO>,
                response: Response<RoomCreateResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.i("createChatRoom : ", response.body().toString())
                }
                // 네비게이션 이벤트 발생
                _navigateToChatFragment.value = true
            }
            override fun onFailure(call: Call<RoomCreateResponseDTO>, t: Throwable) {
                Log.e("채팅 목록 페이지 응답 실패 ", t.toString())
            }
        })
    }

    fun resetNavigation() {
        _navigateToChatFragment.value = false
    }

    fun resetDialog() {
        _shouldOpenCompanionConfirmDialog.value = false
    }

    fun setChatDetailDTO(roomDetailDTO: RoomDetailDTO) {
        latestRoomDetailDTO.value = roomDetailDTO
    }
}