package com.otclub.humate.mate.data

/**
 * 매칭글 작성 정보(매장 및 팝업스토어) Request DTO
 * @author 김지현
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05   김지현        최초 생성
 * </pre>
 */
data class PostWritePlaceRequestDTO(
    // 장소 타입 (1-매장, 2-팝업스토어)
    var type: Int?,
    // 장소 이름
    var name: String?
)
