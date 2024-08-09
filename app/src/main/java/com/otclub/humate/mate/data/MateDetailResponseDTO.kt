package com.otclub.humate.mate.data

/**
 * 매칭글 작성자 프로필 Response DTO
 * @author 조영욱
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05   조영욱        최초 생성
 * </pre>
 */
data class MateDetailResponseDTO(
    // 회원 ID
    val memberId: String,
    // 회원 프로필 이미지
    val profileImgUrl: String?,
    // 회원 닉네임
    val nickname: String,
    // 회원 성별
    val gender: String,
    // 메이트 매칭 날짜
    val matchingDate: String
)
