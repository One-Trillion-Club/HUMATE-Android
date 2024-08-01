package com.otclub.humate.mission.data

data class NewMissionDetailsDTO(
    val activityId: Int,
    val title: String,
    val content: String,
    val point: Int,
    val imgUrl: String
)
