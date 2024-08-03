package com.otclub.humate.mate.data

import java.util.Date

data class PostListResponseDTO(
    val postId: Int,
    val nickname: String,
    val profileImgUrl: String,
    val tags: List<String>,
    val title: String,
    val matchDate: String,
    val matchBranch: String,
    val matchGender: Int,
    val matchLanguage: String,
    val createdAt: String,
    val isMatched: Int
)

/*
프로필 사진 - ImageView, profile_image
이름 - TextView, nickname
게시글 제목 - TextView, title
상태 - TextView, is_matched
매칭 날짜 - TextView, match_date
매칭 지점 - TextView, match_branch
매칭 언어 - TextView, match_language
태그 리스트 - LinearLayout, tag_list
 */