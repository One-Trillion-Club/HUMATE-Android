package com.otclub.humate.mission.data

/**
 * 완료된 활동 상세 dto
 * @author 손승완
 * @since 2024.08.02
 * @version 1.1
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01 	손승완        최초 생성
 * 2024.08.06 	손승완        매칭글 제목 다국어 처리
 * </pre>
 */
data class ClearedMissionDetailsDTO(
   // 매칭글 한국어 제목
   val activityTitleKo: String,
   // 매칭글 영어 제목
   val activityTitleEn: String,
   // 활동 수행 내역 생성일
   val createdAt: String,
   // 활동 수행 내역 이미지 목록
   val imgUrls: List<String>
)
