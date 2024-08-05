package com.otclub.humate.mate.data

data class PostWriteRequestDTO(
    val postId: Int?,
    val memberId: String?,
    val title: String?,
    val content: String?,
    val matchDate: String?,
    val matchBranch: String?,
    val matchGender: Int?,
    val matchLanguage: String?,
    val postPlaces: List<PostWritePlaceRequestDTO>,
    val postTags: List<PostWriteTagRequestDTO>
)