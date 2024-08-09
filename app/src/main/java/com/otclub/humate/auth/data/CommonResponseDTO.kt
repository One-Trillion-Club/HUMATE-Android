package com.otclub.humate.auth.data

/**
 * 성공 여부, 메시지  Response DTO
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
data class CommonResponseDTO(
    // 메시지
    val message: String,
    // 성공 여부
    val success: Boolean
)