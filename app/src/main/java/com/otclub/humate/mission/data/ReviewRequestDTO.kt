package com.otclub.humate.mission.data

/**
 * 후기 요청 dto
 * @author 손승완
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04 	손승완        최초 생성
 * </pre>
 */
data class ReviewRequestDTO(
    // 동행 ID
    val companionId: Int,
    // 후기 내용
    val content: String,
    // 후기 점수
    val score: Double
)
