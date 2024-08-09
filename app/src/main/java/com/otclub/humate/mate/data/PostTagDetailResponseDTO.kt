package com.otclub.humate.mate.data

/**
 * 매칭글 상세 정보(태그) Response DTO
 * @author 김지현
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   김지현        최초 생성
 * </pre>
 */
data class PostTagDetailResponseDTO(
    // 태그 카테고리 (쇼핑, 식사, 행사)
    val category: String,
    // 태그 이름
    val name: String
)
