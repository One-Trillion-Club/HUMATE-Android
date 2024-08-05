package com.otclub.humate.mate.data

data class MateDetailResponseDTO(
    val memberId: String,
    val profileImgUrl: String?,
    val nickname: String,
    val gender: String,
    val matchingDate: String
)
