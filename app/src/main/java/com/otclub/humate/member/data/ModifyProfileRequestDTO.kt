package com.otclub.humate.member.data

/**
 * 내 정보 수정 Request DTO
 * 프로필 이미지는 따로 Multipart File로 전송
 * @author 조영욱
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05  	조영욱        최초 생성
 * </pre>
 */
data class ModifyProfileRequestDTO (
    var nickname: String?,
    var introduction: String?
)