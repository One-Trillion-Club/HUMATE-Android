package com.otclub.humate.mate.data

/**
 * 매칭글 상세 정보(매장 및 팝업스토어) Response DTO
 * @author 김지현
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   김지현        최초 생성
 * </pre>
 */
data class PostPlaceDetailResponseDTO (
    // 장소 타입 (1-매장, 2-팝업스토어)
    val type: Int,
    // 장소 이름
    val name: String
)