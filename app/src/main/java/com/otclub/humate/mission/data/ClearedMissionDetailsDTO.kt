package com.otclub.humate.mission.data

import java.util.Date

data class ClearedMissionDetailsDTO(
   val activityTitle: String,
   val createdAt: Date,
   val imgUrls: List<String>
)
