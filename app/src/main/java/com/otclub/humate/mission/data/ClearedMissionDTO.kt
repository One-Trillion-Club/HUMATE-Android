package com.otclub.humate.mission.data

/**
 * 완료된 활동 dto
 * @author 손승완
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.06 	손승완        매칭글 제목 다국어 처리
 * </pre>
 */
data class ClearedMissionDTO(
    // 활동 수행 내역 ID
    val companionActivityId: Int,
    // 매칭글 한국어 제목
    val titleKo: String,
    // 매칭글 영어 제목
    val titleEn: String,
    // 활동 상태
    val status: Int,
    // 활동 썸네일 이미지 url
    val imgUrl: String?
)
