package com.otclub.humate.mission.data

data class MissionResponseDTO(
    val isFinished: Int,
    val postTitle: String,
    val clearedMissionList: List<ClearedMissionDTO>,
    val newMissionList: List<NewMission>
)
