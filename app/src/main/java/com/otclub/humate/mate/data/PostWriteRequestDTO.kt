package com.otclub.humate.mate.data

/**
 * 매칭글 작성 정보 Request DTO
 * @author 김지현
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05   김지현        최초 생성
 * </pre>
 */
data class PostWriteRequestDTO(
    // 매칭글 ID
    val postId: Int?,
    // 회원 ID
    val memberId: String?,
    // 매칭글 제목
    val title: String?,
    // 매칭글 내용
    val content: String?,
    // 매칭 날짜
    val matchDate: String?,
    // 매칭 지점
    val matchBranch: String?,
    // 매칭 성별
    val matchGender: Int?,
    // 매칭 언어
    val matchLanguage: String?,
    // 매칭 장소(매장 및 팝업스토어) 리스트
    val postPlaces: List<PostWritePlaceRequestDTO>,
    // 매칭 태그 리스트
    val postTags: List<PostWriteTagRequestDTO>
)