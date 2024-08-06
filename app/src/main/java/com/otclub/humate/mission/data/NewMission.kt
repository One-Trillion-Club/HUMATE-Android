package com.otclub.humate.mission.data

data class NewMission(
    val activityId: Int,
    val titleKo: String,
    val titleEn: String,
    val contentKo: String?,
    val contentEn: String?,
    val point: Int,
    val imgUrl: String
)
