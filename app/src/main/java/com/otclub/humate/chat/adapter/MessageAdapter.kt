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

/**
 * 채팅 메세지 Adapter
 *
 * @author 최유경
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	최유경        최초 생성
 * 2024.08.05   최유경        공지 추가
 * 2024.08.07   최유경        프로필 관련 추가
 * </pre>
 */
class MessageAdapter(private val messages: MutableList<Message>, private var roomDetailDTO: RoomDetailDTO?, private val onMateClick: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MESSAGE_RECEIVED = 0
        private const val TYPE_MESSAGE_SENT = 1
        private const val TYPE_MESSAGE_NOTICE = 2
    }

    /**
     * viewHolder 생성
     */
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

        /**
         * ViewHolder 구성해주는 메서드
         */
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messages[position]
            when (holder) {
                is ReceivedMessageViewHolder -> holder.bind(message)
                is SentMessageViewHolder -> holder.bind(message)
                is NoticeMessageViewHolder -> holder.bind(message)
            }
        }

        /**
         * 메세지 타입을 구분하는 메서드
         */
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

        /**
         * 웹소켓으로 메세지 받으면 view 맨 앞에 추가
         */
        fun addMessage(messageWebSocketResponseDTO: MessageWebSocketResponseDTO) {
            messages.add(messageWebSocketResponseDTO.message)

            roomDetailDTO = messageWebSocketResponseDTO.roomDetailDTO // 지울거면 이거

            notifyItemInserted(messages.size - 1)
        }

        /**
         * 채팅 내역 리스트 업데이트
         */
        fun updateMessages(newMessages: List<Message>, detailDTO: RoomDetailDTO? ) {
            messages.clear()
            messages.addAll(newMessages)

            roomDetailDTO = detailDTO
            notifyDataSetChanged()
        }

        /**
         * 수신 받은 메세지에 대한 view Holder
         */
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
                    Glide.with(profileImage)
                        .load(roomDetailDTO?.targetProfileImgUrl)
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

    /**
     * 전송한 메세지에 대한 view Holder
     */
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.message_text)
            private val dateView: TextView = itemView.findViewById(R.id.message_time)

            fun bind(message: Message) {
                textView.text = "${message.content}"
                dateView.text = formatDate(message.createdAt)
            }
        }

    /**
     * 공지 메세지 view Holder
     */
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

    /**
     * 날짜 형식 변환을 위한 메서드
     */
    private fun formatDate(createdAt: String): String {
        // 입력 문자열을 "yyyy-MM-dd HH:mm:ss" 형식으로 파싱
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date: Date = sourceFormat.parse(createdAt)

        // Date 객체를 원하는 형식의 문자열로 변환
        val targetFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate: String = targetFormat.format(date)

        // AM/PM을 소문자로 변환
        return formattedDate.toLowerCase(Locale.getDefault())
    }

    /**
     * 공지인지 확인하는 메서드
     */
    private fun isNotice(msgType : MessageType) : Boolean {
        if(MessageType.TEXT == msgType || MessageType.IMAGE == msgType)
            return false;
        return true;
    }


}