package com.otclub.humate.auth.data

/**
 * 회원가입 Request DTO
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
data class SignUpRequestDTO(
    // 국적 코드
    var nationality: Int? = null,
    // 로그인 아이디
    var loginId: String? = null,
    // 비밀번호
    var password: String? = null,
    // 성별
    var gender: String? = null,
    // 생일
    var birthdate: String? = null,
    // 닉네임
    var nickname: String? = null,
    // 한 줄 소개
    var introduction: String? = null,
    // 사용 가능 언어
    var language: String? = null,
    // 전화번호(한국인만 전송)
    var phone: String? = null,
    // 여권번호(외국인만 전송)
    var passportNo: String? = null,
    // 휴대전화/여권 인증 성공 시 발급된 코드
    var verifyCode: String? = null
)