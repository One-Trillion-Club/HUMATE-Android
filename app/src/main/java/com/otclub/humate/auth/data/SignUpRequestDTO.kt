package com.otclub.humate.auth.data

data class SignUpRequestDTO(
    var nationality: Int? = null,
    var loginId: String? = null,
    var password: String? = null,
    var gender: String? = null,
    var birthdate: String? = null,
    var nickname: String? = null,
    var introduction: String? = null,
    var language: String? = null,
    var phone: String? = null,
    var passportNo: String? = null,
    var verifyCode: String? = null
)