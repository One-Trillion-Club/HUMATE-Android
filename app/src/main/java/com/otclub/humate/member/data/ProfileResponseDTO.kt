package com.otclub.humate.member.data

/**
 * 회원 프로필 세부 정보 Response DTO
 * @author 조영욱
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04  	조영욱        최초 생성
 * </pre>
 */
data class ProfileResponseDTO (
    // 회원 ID
    val memberId: String,
    // 닉네임
    val nickname: String,
    // 매너 온도
    val manner: Double,
    // 한 줄 소개
    val introduction: String?,
    // 프로필 이미지 url
    val profileImgUrl: String?,
    // 성별
    val gender: String,
    // 생일
    val birthdate: String
)