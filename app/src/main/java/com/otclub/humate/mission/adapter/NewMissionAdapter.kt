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
import com.otclub.humate.mission.adapter.ClearedMissionAdapter.ViewHolder
import com.otclub.humate.mission.data.NewMission

class NewMissionAdapter(private val newMissionList: List<NewMission>) : RecyclerView.Adapter<NewMissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_mission, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mission = newMissionList[position]
        holder.missionTitle.text = mission.title

        if (mission.imgUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(mission.imgUrl)
                .into(holder.missionImage)
        }
    }

    override fun getItemCount(): Int = newMissionList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val missionImage: ImageView = itemView.findViewById(R.id.newMissionImage)
        val missionTitle: TextView = itemView.findViewById(R.id.newMissionTitle)
    }
}