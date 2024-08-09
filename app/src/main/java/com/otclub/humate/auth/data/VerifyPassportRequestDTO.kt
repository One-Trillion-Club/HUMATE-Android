package com.otclub.humate.auth.data

/**
 * 여권 인증 Request DTO
 * @author 조영욱
 * @since 2024.08.06
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.06  	조영욱        최초 생성
 * </pre>
 */
data class VerifyPassportRequestDTO(
    // 생일 ("yyyymmdd")
    val birthDate: String,
    // 국적 코드 (codef가 제공하는 국적 코드 참고)
    val nationality: String,
    // 국적 한글 이름 (codef가 제공하는 국적 한글 이름 참고)
    val country: String,
    // 여권 번호
    val passportNo: String
)
