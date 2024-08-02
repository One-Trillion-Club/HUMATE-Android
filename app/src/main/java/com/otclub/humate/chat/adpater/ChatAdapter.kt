package com.otclub.humate.chat.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otclub.humate.R
import com.otclub.humate.chat.data.ChatMessageResponseDTO

class ChatAdapter(private val messages: MutableList<ChatMessageResponseDTO>, private val myId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MESSAGE_RECEIVED = 0
        private const val TYPE_MESSAGE_SENT = 1
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
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messages[position]
            when (holder) {
                is ReceivedMessageViewHolder -> holder.bind(message)
                is SentMessageViewHolder -> holder.bind(message)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val message = messages[position]
            return if (message.senderId == myId) {
                TYPE_MESSAGE_SENT
            } else {
                TYPE_MESSAGE_RECEIVED
            }
        }

        override fun getItemCount(): Int = messages.size

        fun addMessage(message: ChatMessageResponseDTO) {
            messages.add(message)
            notifyItemInserted(messages.size - 1)
        }

        inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.message_text)

            fun bind(message: ChatMessageResponseDTO) {
                textView.text = "${message.senderId}: ${message.content}"
            }
        }

        inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.message_text)

            fun bind(message: ChatMessageResponseDTO) {
                textView.text = "${message.senderId}: ${message.content}"
            }
        }
}