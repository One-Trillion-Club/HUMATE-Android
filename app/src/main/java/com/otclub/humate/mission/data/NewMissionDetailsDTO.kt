package com.otclub.humate.mission.data

/**
 * 새로운 활동 상세 dto
 * @author 손승완
 * @since 2024.08.01
 * @version 1.1
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.06 	손승완        매칭글 제목 다국어 처리
 * </pre>
 */
data class NewMissionDetailsDTO(
    // 활동 ID
    val activityId: Int,
    // 매칭글 한국어 제목
    val titleKo: String,
    // 매칭글 영어 제목
    val titleEn: String,
    // 매칭글 한국어 내용
    val contentKo: String,
    // 매칭글 영어 내용
    val contentEn: String,
    // 포인트
    val point: Int,
    // 이미지 url
    val imgUrl: String
)
