package com.otclub.humate.member.data

data class ProfileResponseDTO (
    val memberId: String,
    val nickname: String,
    val manner: Double,
    val introduction: String?,
    val profileImgUrl: String?,
    val gender: String,
    val birthdate: String
)