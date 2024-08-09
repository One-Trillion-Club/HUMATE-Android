package com.otclub.humate.auth.data

/**
 * 회원 로그인 Request DTO
 * @author 조영욱
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	조영욱        최초 생성
 * </pre>
 */
data class LoginRequestDTO(
    // 로그인 아이디
    val loginId: String,
    // 비밀번호
    val password: String
)