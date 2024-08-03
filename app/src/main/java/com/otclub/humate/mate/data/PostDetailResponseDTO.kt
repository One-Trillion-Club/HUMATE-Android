package com.otclub.humate.mate.data

import java.util.*

data class PostDetailResponseDTO(
    val postId: Int,
    val memberId: String,
    val profileImgUrl: String,
    val title: String,
    val content: String,
    val matchDate: String,
    val matchBranch: String,
    val matchGender: Int,
    val matchLanguage: String,
    val isMatched: Int,
    val postPlaces: List<PostPlaceDetailResponseDTO>,
    val postTags: List<PostTagDetailResponseDTO>
)