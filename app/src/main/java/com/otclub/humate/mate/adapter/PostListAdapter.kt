package com.otclub.humate.mate.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otclub.humate.R
import com.otclub.humate.mate.data.PostListResponseDTO

class PostListAdapter(
    private var posts: List<PostListResponseDTO>,
    private val onItemClick: (Int) -> Unit // 클릭된 아이템의 id를 전달하도록 수정
) : RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val nickname: TextView = itemView.findViewById(R.id.nickname)
        val title: TextView = itemView.findViewById(R.id.title)
        val isMatched: TextView = itemView.findViewById(R.id.is_matched)
        val matchDate: TextView = itemView.findViewById(R.id.match_date)
        val matchBranch: TextView = itemView.findViewById(R.id.match_branch)
        val matchLanguage: TextView = itemView.findViewById(R.id.match_language)
        val matchGender: TextView = itemView.findViewById(R.id.match_gender)
        val tagList: LinearLayout = itemView.findViewById(R.id.tag_list)

        fun bind(post: PostListResponseDTO) {
            // 데이터를 각 뷰에 바인딩
            nickname.text = post.nickname
            title.text = post.title
            isMatched.text = post.isMatched.toString()
            matchDate.text = post.matchDate?.takeIf { it.isNotBlank() } ?: "-"
            matchBranch.text = post.matchBranch?.takeIf { it.isNotBlank() } ?: "-"
            matchLanguage.text = post.matchLanguage?.takeIf { it.isNotBlank() } ?: "-"
            matchGender.text = when (post.matchGender) {
                1 -> "나랑 같은 성별"
                2 -> "상관 없음"
                else -> "-"
            }

            // 매칭 진행 여부 설정
            isMatched.text = if (post.isMatched == 0) "진행중" else "마감"
            isMatched.setBackgroundResource(if (post.isMatched == 0) R.drawable.post_ongoing else R.drawable.post_closed)

            // 프로필 이미지 세팅
            Glide.with(profileImage.context)
                .load(post.profileImgUrl)
                .placeholder(R.drawable.basic_profile)
                .into(profileImage)

            // 태그 리스트 세팅
            tagList.removeAllViews()
            val tagsToShow = post.tags.take(3) // 최대 3개 태그만 표시

            // 태그 리스트의 Gravity 설정
            tagList.gravity = if (tagsToShow.size <= 2) {
                Gravity.END // 1개 또는 2개 태그를 오른쪽으로 배치
            } else {
                Gravity.START // 3개 이상은 기본 왼쪽 정렬
            }

            tagsToShow.forEach { tag ->
                val tagView = TextView(tagList.context).apply {
                    text = tag
                    setBackgroundResource(R.drawable.post_tag)
                    setPadding(8, 4, 8, 4)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    textSize = 10f
                    gravity = Gravity.CENTER
                    typeface = Typeface.DEFAULT_BOLD

                    // 텍스트의 실제 너비 측정
                    val paint = Paint()
                    paint.textSize = this.textSize
                    val textWidth = paint.measureText(tag)

                    // 여유 공간(패딩)을 포함한 버튼 너비 계산
                    val padding = 80 // 좌우 패딩 (예: 50dp * 2)
                    val tagWidth = textWidth.toInt() + padding

                    // LayoutParams 설정
                    layoutParams = LinearLayout.LayoutParams(
                        tagWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        // 태그들 사이의 간격 설정 (여기서는 오른쪽 마진을 설정함)
                        setMargins(12, 0, 12, 0) // 원하는 간격으로 설정
                    }
                }
                tagList.addView(tagView)
            }

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(post.postId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mate_item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size
}
