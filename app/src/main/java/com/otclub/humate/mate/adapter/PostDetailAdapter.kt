package com.otclub.humate.mate.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.otclub.humate.mate.data.PostDetailResponseDTO

class PostDetailAdapter(
    private val postDetail: PostDetailResponseDTO
) : RecyclerView.Adapter<PostDetailAdapter.PostDetailViewHolder>() {

    inner class PostDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PostDetailViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}
