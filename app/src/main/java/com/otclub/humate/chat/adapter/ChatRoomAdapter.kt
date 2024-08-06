package com.otclub.humate.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.chat.data.ChatRoomDetailDTO

class ChatRoomAdapter(private val roomList: List<ChatRoomDetailDTO>,
                      private val onItemClick: (ChatRoomDetailDTO) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val nickname: TextView = itemView.findViewById(R.id.nickname)
        val title: TextView = itemView.findViewById(R.id.title)
        val latestChat: TextView = itemView.findViewById(R.id.latest_chat)
        val latestTime: TextView = itemView.findViewById(R.id.latest_time)
        val unreadCnt: TextView = itemView.findViewById(R.id.unread_cnt)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_room_item, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val room = roomList[position]

        // 데이터를 각 뷰에 바인딩
        holder.nickname.text = room.targetNickname
        holder.title.text = room.postTitle
        //holder.latestChat.text = room.latestChat.toString()
        //holder.latestTime.text = room.latestTime?.takeIf { it.isNotBlank() } ?: "상관 없음"
        holder.unreadCnt.text = "3"
        Log.d("[onBindViewHolder] room : " , room.toString())
        Log.d("[onBindViewHolder] holder : " , holder.toString())

        // 프로필 이미지 세팅
        Glide.with(holder.profileImage.context)
            .load(room.targetProfileImgUrl)
            .placeholder(R.drawable.ic_member_profile_default)
            .into(holder.profileImage)

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(room)
        }
    }

    override fun getItemCount(): Int = roomList.size
}