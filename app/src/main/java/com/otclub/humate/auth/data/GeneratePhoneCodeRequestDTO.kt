package com.otclub.humate.auth.data

/**
 * 휴대전화 번호 인증 코드 생성 Request DTO
 * @author 조영욱
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02  	조영욱        최초 생성
 * </pre>
 */
data class GeneratePhoneCodeRequestDTO(
    // 휴대전화 번호
    val phone: String
)
