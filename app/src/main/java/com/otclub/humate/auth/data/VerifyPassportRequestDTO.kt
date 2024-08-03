package com.otclub.humate.auth.data

data class VerifyPassportRequestDTO(
    val birthdate: String,
    val nationality: String,
    val country: String,
    val passportNo: String
)
