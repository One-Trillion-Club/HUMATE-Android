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
import com.otclub.humate.mate.data.LocalizedBranch
import com.otclub.humate.mate.data.LocalizedTag
import com.otclub.humate.mate.data.PostListResponseDTO
import com.otclub.humate.sharedpreferences.SharedPreferencesManager

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

        private var sharedPreferencesManager = SharedPreferencesManager(itemView.context)
        val currentLanguage = sharedPreferencesManager.getLanguage()

        private fun getEnglishLanguageName(language: String): String {
            return when (language) {
                "한국어" -> "Korean"
                "영어" -> "English"
                "중국어" -> "Chinese"
                "일본어" -> "Japanese"
                else -> language
            }
        }

        fun bind(post: PostListResponseDTO) {
            // 데이터를 각 뷰에 바인딩
            nickname.text = post.nickname
            title.text = post.title
            isMatched.text = post.isMatched.toString()
            matchDate.text = post.matchDate?.takeIf { it.isNotBlank() } ?: "-"
            matchBranch.text = post.matchBranch?.takeIf { it.isNotBlank() } ?: "-"

            val selectedLanguage = post.matchLanguage?.split(", ") ?: emptyList()
            val englishLanguage = selectedLanguage.map { getEnglishLanguageName(it) }
            val englishLanguageText = if (englishLanguage.isNotEmpty()) {
                englishLanguage.joinToString(", ")
            } else {
                "-"
            }
            matchLanguage.text = if (currentLanguage == 1) {
                post.matchLanguage?.takeIf { it.isNotBlank() } ?: "-"
            } else {
                englishLanguageText
            }

            matchGender.text = when (post.matchGender) {
                1 -> itemView.context.getString(R.string.same_gender)
                2 -> itemView.context.getString(R.string.both_gender)
                else -> "-"
            }

            // 매칭 진행 여부 설정
            val context = itemView.context
            isMatched.text = if (post.isMatched == 0) {
                context.getString(R.string.status_ongoing)
            } else {
                context.getString(R.string.closed)
            }
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
            tagList.gravity = Gravity.START

            tagsToShow.forEach { tag ->
                val tagView = TextView(tagList.context).apply {
                    val displayText = if (currentLanguage != 1) {
                        val localizedTag = LocalizedTag.values().find { it.koreanName == tag }
                        localizedTag?.englishName ?: tag
                    } else {
                        tag
                    }

                    text = displayText
                    setBackgroundResource(R.drawable.post_tag)
                    setPadding(15, 4, 15, 4)
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
                        // 태그들 사이의 간격 설정
                        setMargins(12, 0, 12, 0)

                        // currentLanguage에 따라 다르게 설정
                        if (currentLanguage == 1) {
                            width = tagWidth
                            setPadding(15, 4, 15, 4)
                        } else {
                            width = LinearLayout.LayoutParams.WRAP_CONTENT
                            setPadding(20, 4, 20, 4)
                        }
                    }
                }
                tagList.addView(tagView)
            }

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(post.postId)
            }

            // LocalizedBranch를 사용하여 matchBranch 텍스트 설정
            if (currentLanguage != 1) {
                post.matchBranch?.let { branchName ->
                    val localizedBranch = LocalizedBranch.values().find { it.koreanName == branchName }
                    localizedBranch?.let {
                        matchBranch.text = it.englishName
                    }
                }
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
