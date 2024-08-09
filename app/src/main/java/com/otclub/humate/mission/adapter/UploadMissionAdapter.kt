package com.otclub.humate.mission.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R

/**
 * 활동 업로드 Adapter
 * @author 손승완
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03 	손승완        최초 생성
 * </pre>
 */
class UploadMissionAdapter(private val imageFiles: List<Uri>, val context: Context) :
    RecyclerView.Adapter<UploadMissionAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mission_upload_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = imageFiles[position]
        Glide.with(context).load(image)
            .override(300, 300)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageFiles.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.missionUploadImageView)
    }
}
