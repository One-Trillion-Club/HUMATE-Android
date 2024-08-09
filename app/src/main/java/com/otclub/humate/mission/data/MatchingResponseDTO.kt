package com.otclub.humate.mission.data

/**
 * 동행 정보 dto
 * @author 손승완
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02 	손승완        최초 생성
 * </pre>
 */
data class MatchingResponseDTO(
    // 동행 ID
    val companionId: Int,
    // 매칭글 제목
    val postTitle: String,
    // 메이트 프로필 이미지 url
    val mateProfileImgUrl: String,
    // 메이트 닉네임
    val mateNickname: String,
    // 매칭 날짜
    val matchDate: String,
    // 매칭 지점
    val matchBranch: String,
    // 동행 상태
    val status: String
)
