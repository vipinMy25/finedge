package com.moreyeahs.financeapp.data.remote.dto.request

data class VerifyOtpRequest(
    val Email: String,
    val otp: String
)
