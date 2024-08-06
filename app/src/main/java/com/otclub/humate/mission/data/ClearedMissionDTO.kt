package com.otclub.humate.mission.data

data class ClearedMissionDTO(
    val companionActivityId: Int,
    val titleKo: String,
    val titleEn: String,
    val status: Int,
    val imgUrl: String?
)
