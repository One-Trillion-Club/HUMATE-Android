package com.otclub.humate.auth.data

/**
 * 휴대전화 번호 인증 Request DTO
 * @author 조영욱
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03  	조영욱        최초 생성
 * </pre>
 */
data class VerifyPhoneCodeRequestDTO(
    // 휴대전화 번호
    val phone: String,
    // 인증 코드
    val code: String
)
