package com.otclub.humate.mate.data

import java.util.*

data class PostDetailResponseDTO(
    val postId: Int,
    val memberId: String,
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
