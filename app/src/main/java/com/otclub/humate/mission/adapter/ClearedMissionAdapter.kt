package com.otclub.humate.mission.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.mission.data.ClearedMissionDTO

class ClearedMissionAdapter(private val clearedMissionList: List<ClearedMissionDTO>) : RecyclerView.Adapter<ClearedMissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cleared_mission, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mission = clearedMissionList[position]
        holder.missionTitle.text = mission.title
        holder.missionStatus.text = when (mission.status) {
            0 -> "완료"
            1 -> "진행 중"
            else -> "대기 중"
        }

        // 이미지 URL이 있는 경우 Glide로 이미지 로딩
        if (mission.imgUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(mission.imgUrl)
                .into(holder.missionImage)
        }
    }

    override fun getItemCount(): Int {
        Log.i("missionListSize : ", clearedMissionList.size.toString())
        return clearedMissionList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val missionImage: ImageView = itemView.findViewById(R.id.missionImage)
        val missionTitle: TextView = itemView.findViewById(R.id.missionTitle)
        val missionStatus: TextView = itemView.findViewById(R.id.missionStatus)
    }
}