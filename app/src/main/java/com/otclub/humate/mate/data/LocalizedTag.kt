package com.otclub.humate.mate.data

/**
 * 매칭 태그(한-영) Enum
 * @author 김지현
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.07   김지현        최초 생성
 * </pre>
 */
enum class LocalizedTag(val englishName: String, val koreanName: String, val id: Int)  {
    // 쇼핑 태그
    CLOTHING("clothing", "의류", 1),
    BEAUTY("beauty", "뷰티", 2),
    ACCESSORY("accessory", "악세서리", 3),
    FOOTWEAR("footwear", "신발류", 4),

    // 식사 태그
    KOREAN_FOOD("korean", "한식", 5),
    JAPANESE_FOOD("japanese", "일식", 6),
    WESTERN_FOOD("western", "양식", 7),
    CHINESE_FOOD("chinese", "중식", 8),
    SNACK("snack", "분식", 9),

    // 행사 태그
    POPUP_STORE("pop-up", "팝업스토어", 10),
    EXHIBITION("exhibition", "전시", 11),
    PERFORMANCE("performance", "공연", 12);

    companion object {
        fun fromId(id: Int): LocalizedTag? {
            return values().firstOrNull { it.id == id }
        }

        fun fromName(name: String): LocalizedTag? {
            return values().firstOrNull { it.englishName == name || it.koreanName == name }
        }
    }

    fun getName(languageCode: Int): String {
        return if (languageCode == 1) koreanName else englishName
    }

}
