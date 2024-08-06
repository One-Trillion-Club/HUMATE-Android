package com.otclub.humate.member.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.databinding.MemberItemMateBinding
import com.otclub.humate.mate.data.MateDetailResponseDTO

class MateListAdapter(
    private var mates: List<MateDetailResponseDTO>,
    private val onMateClick: (String) -> Unit
): RecyclerView.Adapter<MateListAdapter.MateViewHolder>() {

    // inner class
    inner class MateViewHolder(binding: MemberItemMateBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage = binding.mateListProfileImage
        val nickname = binding.mateListNicknameText
        val matchingDate = binding.mateListMatchingDateText

        val root = binding.root

        fun bind(mate: MateDetailResponseDTO) {

            // 프로필 이미지 세팅
            Glide.with(profileImage.context)
                .load(mate.profileImgUrl)
                .into(profileImage)

            nickname.setText(mate.nickname)
            matchingDate.setText(mate.matchingDate.split(" ")[0].replace("-", "."))

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                onMateClick(mate.memberId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateViewHolder {
        val binding: MemberItemMateBinding = MemberItemMateBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return MateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MateViewHolder, position: Int) {
        val mate = mates[position]
        holder.bind(mate)
    }

    override fun getItemCount(): Int = mates.size
}