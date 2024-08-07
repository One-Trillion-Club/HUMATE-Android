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
import com.otclub.humate.chat.data.ChatMessageResponseDTO
import com.otclub.humate.chat.data.MessageType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val messages: MutableList<ChatMessageResponseDTO>, private val participateId: String?, private val onMateClick: (String) -> Unit) :
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
                if (message.participateId.equals(participateId)) {
                    TYPE_MESSAGE_SENT
                } else {
                    TYPE_MESSAGE_RECEIVED
                }
            }
        }

        override fun getItemCount(): Int = messages.size

        fun addMessage(message: ChatMessageResponseDTO) {
            messages.add(message)
            notifyItemInserted(messages.size - 1)
        }

        fun updateMessages(newMessages: List<ChatMessageResponseDTO>) {
            messages.clear()
            messages.addAll(newMessages)
            notifyDataSetChanged()
        }

        inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.message_text)
            private val dateView: TextView = itemView.findViewById(R.id.message_time)
            private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)

            fun bind(message: ChatMessageResponseDTO) {
                textView.text = message.content
                dateView.text = formatDate(message.createdAt)

                profileImage.setOnClickListener {
                    onMateClick("K_1")
                }

                val imgUrl = message.imgUrl
                if (imgUrl != null) {
                    Log.d("[ReceivedMessageViewHolder]", imgUrl)
                    Glide.with(profileImage) // Context는 itemView의 Context를 사용합니다.
                        .load(message.imgUrl) // URL을 message.imgUrl로 설정합니다.
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

            fun bind(message: ChatMessageResponseDTO) {
                textView.text = "${message.content}"
                dateView.text = formatDate(message.createdAt)
            }
        }

        inner class NoticeMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            private val textView: TextView = itemView.findViewById(R.id.message_notice)
            fun bind(message: ChatMessageResponseDTO) {
                val context = itemView.context
                val str = when (message.messageType) {
                    MessageType.MATE_ACTIVE -> context.getString(R.string.chat_mate_active_message)
                    MessageType.MATE_INACTIVE -> context.getString(R.string.chat_mate_inactive_message)
                    else -> ""
                }
                textView.text = "${message.participateId} 님이 $str"
            }
        }

    // 날짜 형식 변환을 위한 메서드
    private fun formatDate(createdAt: String): String {
        // Step 1: 타임스탬프 문자열을 Long 타입으로 변환
        val timestamp = createdAt.toLong()

        // Step 2: Long 타입의 타임스탬프를 Date 객체로 변환
        val date = Date(timestamp)

        // Step 3: Date 객체를 원하는 형식의 문자열로 변환
        val targetFormat = SimpleDateFormat("hh:mm a")
        val formattedDate: String = targetFormat.format(date)

        // AM/PM을 소문자로 변환
        return formattedDate.toLowerCase(Locale.getDefault())
    }


    private fun isNotice(msgType : MessageType) : Boolean {
        if(MessageType.TEXT.equals(msgType) || MessageType.IMAGE.equals(msgType))
            return false;
        return true;
    }



}