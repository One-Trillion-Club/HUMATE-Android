package com.otclub.humate.mate.data

/**
 * 매칭 장소(한-영) Enum
 * @author 김지현
 * @since 2024.08.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.07   김지현        최초 생성
 * </pre>
 */
enum class LocalizedBranch(val englishName: String, val koreanName: String, val id: Int)  {
    THE_HYUNDAI_SEOUL("The Hyundai Seoul", "더현대 서울", 1),
    THE_HYUNDAI_DAEGU("The Hyundai Daegu", "더현대 대구", 2),
    APGUJEONG("Apgujeong", "압구정본점", 3),
    TRADE_CENTER("Trade Center", "무역센터점", 4),
    CHEONHO("Chenho", "천호점", 5),
    SINCHON("Sinchon", "신촌점", 6),
    MIA("Mia", "미아점", 7),
    MOKDONG("Mokdong","목동점", 8),
    JUNGDONG("Jungdong", "중동점", 9),
    KINTEX("KINTEX", "킨텍스", 10),
    D_CUBE("D Cube", "디큐브", 11),
    PANGYO("Pangyo", "판교점", 12),
    BUSAN("Busan", "부산점", 13),
    ULSAN("Ulsan", "울산점", 14),
    ULSAN_DONG_GU("Ulsan Dong-gu", "울산동구점", 15),
    CHUNGCHEONG("ChungCheong", "충청점", 16);


    companion object {
        fun fromId(id: Int): LocalizedBranch? {
            return values().firstOrNull { it.id == id }
        }

        fun fromName(name: String): LocalizedBranch? {
            return values().firstOrNull { it.englishName == name || it.koreanName == name }
        }
    }

    fun getName(languageCode: Int): String {
        return if (languageCode == 1) koreanName else englishName
    }

}
