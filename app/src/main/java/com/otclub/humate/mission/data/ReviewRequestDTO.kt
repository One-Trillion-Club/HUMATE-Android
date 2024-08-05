package com.otclub.humate.mission.data

data class ReviewRequestDTO(
    val companionId: Int,
    val content: String,
    val score: Int
)
