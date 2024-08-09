package com.otclub.humate.mate.data

/**
 * 매칭글 조회 목록 Response DTO
 * @author 김지현
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   김지현        최초 생성
 * </pre>
 */
data class PostListResponseDTO(
    // 매칭글 ID
    val postId: Int,
    // 매칭글 작성자 닉네임
    val nickname: String,
    // 매칭글 작성자 프로필 이미지
    val profileImgUrl: String,
    // 매칭글 태그
    val tags: List<String>,
    // 매칭글 제목
    val title: String,
    // 매칭 날짜
    val matchDate: String,
    // 매칭 지점
    val matchBranch: String,
    // 매칭 성별
    val matchGender: Int,
    // 매칭 언어
    val matchLanguage: String,
    // 매칭글 작성 날짜
    val createdAt: String,
    // 매칭 여부
    val isMatched: Int
)