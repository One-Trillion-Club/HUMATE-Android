package com.otclub.humate.mate.data

/**
 * 매칭글 상세 정보 Response DTO
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
data class PostDetailResponseDTO(
    // 매칭글 ID
    val postId: Int,
    // 회원 ID
    val memberId: String,
    // 회원 프로필 이미지
    val profileImgUrl: String,
    // 매칭글 제목
    val title: String,
    // 매칭글 내용
    val content: String,
    // 매칭 날짜
    val matchDate: String,
    // 매칭 지점
    val matchBranch: String,
    // 매칭 성별
    val matchGender: Int,
    // 매칭 언어
    val matchLanguage: String,
    // 매칭 여부
    val isMatched: Int,
    // 장소 및 팝업스토어
    val postPlaces: List<PostPlaceDetailResponseDTO>,
    // 태그
    val postTags: List<PostTagDetailResponseDTO>
)