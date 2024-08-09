package com.otclub.humate.mission.data

/**
 * 활동(완료된, 새로운) 목록 dto
 * @author 손승완
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * </pre>
 */
data class MissionResponseDTO(
    val isFinished: Int,
    val postTitle: String,
    val clearedMissionList: List<ClearedMissionDTO>,
    val newMissionList: List<NewMissionDetailsDTO>
)
