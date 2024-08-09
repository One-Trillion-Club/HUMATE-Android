package com.otclub.humate.mate.data

/**
 * 태그 정보
 * @author 김지현
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------------
 * 2024.08.04   김지현        최초 생성
 * 2024.08.06   김지현        LocalizedTag를 사용한 버튼 필드 추가
 * 2024.08.07   김지현        태그 이름 필드 추가
 * </pre>
 */
data class Tag(
    val iconResId: Int,
    val titleResId: Int,
    val buttons: List<LocalizedTag>
)
