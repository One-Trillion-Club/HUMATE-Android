package com.otclub.humate.mate.data

/**
 * 매칭글 작성 정보(태그) Request DTO
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
data class PostWriteTagRequestDTO(
    // 태그 ID
    val tagId: Int?
)
