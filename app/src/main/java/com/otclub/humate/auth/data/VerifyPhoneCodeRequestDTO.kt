package com.otclub.humate.auth.data

data class VerifyPhoneCodeRequestDTO(
    val phone: String,
    val code: String
)
