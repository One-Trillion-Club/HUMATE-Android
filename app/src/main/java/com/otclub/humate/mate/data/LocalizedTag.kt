package com.otclub.humate.mate.data

import java.util.*

enum class LocalizedTag(val englishName: String, val koreanName: String, val id: Int)  {
    // 쇼핑 태그
    CLOTHING("Clothing", "의류", 1),
    BEAUTY("Beauty", "뷰티", 2),
    ACCESSORY("Accessory", "악세서리", 3),
    FOOTWEAR("Footwear", "신발류", 4),

    // 식사 태그
    KOREAN_FOOD("Korean", "한식", 5),
    JAPANESE_FOOD("Japanese", "일식", 6),
    WESTERN_FOOD("Western", "양식", 7),
    CHINESE_FOOD("Chinese", "중식", 8),
    SNACK("Snack", "분식", 9),

    // 행사 태그
    POPUP_STORE("Popup Store", "팝업스토어", 10),
    EXHIBITION("Exhibition", "전시", 11),
    PERFORMANCE("Performance", "공연", 12);

    companion object {
        fun fromId(id: Int): LocalizedTag? {
            return values().firstOrNull { it.id == id }
        }

        fun fromName(name: String, locale: Locale): String {
            return values().firstOrNull { it.englishName == name || it.koreanName == name }?.let {
                when (locale.language) {
                    Locale.KOREAN.language -> it.koreanName
                    else -> it.englishName
                }
            } ?: name // 태그가 Enum에 없으면 원래 이름을 반환
        }
    }
}
