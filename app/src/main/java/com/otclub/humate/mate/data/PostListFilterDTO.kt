package com.otclub.humate.mate.data

data class PostListFilterDTO(
    val keyword: String?,
    val tagName: String?,
    val matchDate: String?,
    val matchBranch: String?,
    val matchGender: String?,
    val matchLanguage: String?,
)
