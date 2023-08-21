package com.moreyeahs.financeapp.data.remote.dto.request

data class AuthRegisterRequest(
    val fullName: String,
    val Email: String,
    val password: String,
    val confirm_password: String,
    val Mobile_No: String
)
