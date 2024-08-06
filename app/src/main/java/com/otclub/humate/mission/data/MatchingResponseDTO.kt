package com.otclub.humate.mission.data

data class MatchingResponseDTO(
    val companionId: Int,
    val postTitle: String,
    val mateProfileImgUrl: String,
    val mateNickname: String,
    val matchDate: String,
    val matchBranch: String,
    val status: String
)
