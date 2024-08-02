package com.otclub.humate.mission.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.mission.data.MatchingResponseDTO

class MatchingAdapter(
    private var matchings: List<MatchingResponseDTO>,
    param: (Any) -> Unit
) : RecyclerView.Adapter<MatchingAdapter.MatchingViewHolder>() {

    inner class MatchingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val mateProfileImg: ImageView = itemView.findViewById(R.id.mateProfileImg)
        val mateNickname: TextView = itemView.findViewById(R.id.mateNickname)
        val matchDate: TextView = itemView.findViewById(R.id.matchDate)
        val status: TextView = itemView.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_matching_item, parent, false)
        return MatchingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchingViewHolder, position: Int) {
        val matching = matchings[position]
        holder.postTitle.text = matching.postTitle
        holder.mateNickname.text = matching.mateNickname
        holder.matchDate.text = matching.matchDate
        holder.status.text = matching.status
        Glide.with(holder.itemView.context)
            .load(matching.mateProfileImgUrl)
            .into(holder.mateProfileImg)
    }

    override fun getItemCount(): Int = matchings.size

    fun updateData(newMatchings: List<MatchingResponseDTO>) {
        matchings = newMatchings
        notifyDataSetChanged()
    }
}
