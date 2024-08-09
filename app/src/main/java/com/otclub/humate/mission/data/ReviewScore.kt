package com.otclub.humate.mission.data

/**
 * 후기 점수 enum class
 * @author 손승완
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05 	손승완        최초 생성
 * </pre>
 */
enum class ReviewScore(val weight: Double) {
    EXCELLENT(0.2),
    GOOD(0.1),
    BAD(-0.2)
}
