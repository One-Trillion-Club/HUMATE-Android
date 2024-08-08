package com.otclub.humate.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.otclub.humate.R
import com.otclub.humate.chat.data.Message
import com.otclub.humate.chat.data.MessageWebSocketResponseDTO
import com.otclub.humate.chat.data.RoomDetailDTO
import com.otclub.humate.chat.data.MessageType
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messages: MutableList<Message>, private var roomDetailDTO: RoomDetailDTO?, private val onMateClick: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MESSAGE_RECEIVED = 0
        private const val TYPE_MESSAGE_SENT = 1
        private const val TYPE_MESSAGE_NOTICE = 2
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_MESSAGE_RECEIVED -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_received, parent, false)
                    ReceivedMessageViewHolder(view)
                }
                TYPE_MESSAGE_SENT -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_sent, parent, false)
                    SentMessageViewHolder(view)
                }
                TYPE_MESSAGE_NOTICE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_notice, parent, false)
                    NoticeMessageViewHolder(view)
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messages[position]
            when (holder) {
                is ReceivedMessageViewHolder -> holder.bind(message)
                is SentMessageViewHolder -> holder.bind(message)
                is NoticeMessageViewHolder -> holder.bind(message)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val message = messages[position]
            return if(isNotice(message.messageType)){
                TYPE_MESSAGE_NOTICE
            }
            else{
                if (message.participateId.equals(roomDetailDTO?.participateId)) {
                    TYPE_MESSAGE_SENT
                } else {
                    TYPE_MESSAGE_RECEIVED
                }
            }
        }

        override fun getItemCount(): Int = messages.size

        fun addMessage(messageWebSocketResponseDTO: MessageWebSocketResponseDTO) {
            messages.add(messageWebSocketResponseDTO.message)

            roomDetailDTO = messageWebSocketResponseDTO.roomDetailDTO // 지울거면 이거

            notifyItemInserted(messages.size - 1)
        }

        fun updateMessages(newMessages: List<Message>, detailDTO: RoomDetailDTO? ) {
            messages.clear()
            messages.addAll(newMessages)

            roomDetailDTO = detailDTO
            notifyDataSetChanged()
        }

//        fun updateMessages(newMessages: List<ChatMessage>, chatRoomDetailDTO: ChatRoomDetailDTO? ) {
//            messages.clear()
//            messages.addAll(newMessages)
//            notifyDataSetChanged()
//        }


        inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.message_text)
            private val dateView: TextView = itemView.findViewById(R.id.message_time)
            private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)

            fun bind(message: Message) {
                textView.text = message.content
                dateView.text = formatDate(message.createdAt)

                profileImage.setOnClickListener {
                    onMateClick(roomDetailDTO?.targetMemberId!!)
                }

                val imgUrl = roomDetailDTO?.targetProfileImgUrl
                if (imgUrl != null) {
                    Log.d("[ReceivedMessageViewHolder]", roomDetailDTO?.targetProfileImgUrl.toString())
                    Glide.with(profileImage) // Context는 itemView의 Context를 사용합니다.
                        .load(roomDetailDTO?.targetProfileImgUrl) // URL을 message.imgUrl로 설정합니다.
                        .apply(
                            RequestOptions()
                                .circleCrop()
                                .placeholder(R.drawable.ic_member_profile_default)
                        )
                        .placeholder(R.drawable.ic_member_profile_default)
                        .into(profileImage)
                } else {
                    Log.d("[ReceivedMessageViewHolder]", "Image URL is null")
                }
            }
        }

        inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.message_text)
            private val dateView: TextView = itemView.findViewById(R.id.message_time)

            fun bind(message: Message) {
                textView.text = "${message.content}"
                dateView.text = formatDate(message.createdAt)
            }
        }

        inner class NoticeMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            private val textView: TextView = itemView.findViewById(R.id.message_notice)
            fun bind(message: Message) {
                val context = itemView.context
                val str = when (message.messageType) {
                    MessageType.MATE_ACTIVE -> context.getString(R.string.chat_mate_active_message)
                    MessageType.MATE_INACTIVE -> context.getString(R.string.chat_mate_inactive_message)
                    else -> ""
                }
                textView.text = "${message.content} $str"
            }
        }

    // 날짜 형식 변환을 위한 메서드
    private fun formatDate(createdAt: String): String {
        // Step 1: 입력 문자열을 "yyyy-MM-dd HH:mm:ss" 형식으로 파싱
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date: Date = sourceFormat.parse(createdAt)

        // Step 2: Date 객체를 원하는 형식의 문자열로 변환
        val targetFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate: String = targetFormat.format(date)

        // AM/PM을 소문자로 변환
        return formattedDate.toLowerCase(Locale.getDefault())
    }

    private fun isNotice(msgType : MessageType) : Boolean {
        if(MessageType.TEXT == msgType || MessageType.IMAGE == msgType)
            return false;
        return true;
    }


}