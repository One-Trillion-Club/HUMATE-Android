package com.otclub.humate.mate.data

/**
 * 매칭글 조회 필터 DTO
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
data class PostListFilterDTO(
    // 검색 키워드
    val keyword: String?,
    // 태그 이름
    val tagName: String?,
    // 매칭 날짜
    val matchDate: String?,
    // 매칭 지점
    val matchBranch: String?,
    // 매칭 성별
    val matchGender: String?,
    // 매칭 언어
    val matchLanguage: String?
)
