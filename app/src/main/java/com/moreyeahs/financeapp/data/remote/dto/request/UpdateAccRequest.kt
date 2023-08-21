package com.moreyeahs.financeapp.data.remote.dto.request

data class UpdateAccRequest(
    val user_Id: String,
    val _id: String,
    val is_default: Boolean,
    val userAccountName: String
)