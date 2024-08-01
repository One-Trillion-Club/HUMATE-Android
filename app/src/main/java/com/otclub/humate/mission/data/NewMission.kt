package com.otclub.humate.mission.data

data class NewMission(
    val activityId: Int,
    val title: String,
    val content: String?,
    val point: Int,
    val imgUrl: String
)
