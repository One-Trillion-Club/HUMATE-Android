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
import com.otclub.humate.chat.data.RoomDetailDTO
import java.text.SimpleDateFormat
import java.util.*

/**
 * 채팅방 Adapter
 *
 * @author 최유경
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04  	최유경        최초 생성
 * 2024.08.07   최유경        프로필 기능 추가
 * </pre>
 */
class RoomAdapter(private val roomList: List<RoomDetailDTO>,
                  private val onItemClick: (RoomDetailDTO) -> Unit
) : RecyclerView.Adapter<RoomAdapter.ChatRoomViewHolder>() {

    class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val nickname: TextView = itemView.findViewById(R.id.nickname)
        val title: TextView = itemView.findViewById(R.id.title)
        val latestChat: TextView = itemView.findViewById(R.id.latest_chat)
        //val latestTime: TextView = itemView.findViewById(R.id.latest_time)
        val unreadCnt: TextView = itemView.findViewById(R.id.unread_cnt)
    }


    /**
     * viewHolder 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_main_item, parent, false)
        return ChatRoomViewHolder(view)
    }

    /**
     * viewHolder 구성해주는 메서드
     */
    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val room = roomList[position]

        // 데이터를 각 뷰에 바인딩
        holder.nickname.text = room.targetNickname
        holder.title.text = room.postTitle
        holder.latestChat.text = room.latestContent
        //holder.latestTime.text =  formatDate(room.latestContentTime)
        holder.unreadCnt.text = "1"
        Log.d("[onBindViewHolder] room : " , room.toString())
        Log.d("[onBindViewHolder] holder : " , holder.toString())

        // 프로필 이미지 세팅
        Glide.with(holder.profileImage.context)
            .load(room.targetProfileImgUrl)
            .apply(
                RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_member_profile_default)
            )
            .placeholder(R.drawable.ic_member_profile_default)
            .into(holder.profileImage)

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(room)
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

    override fun getItemCount(): Int = roomList.size
}