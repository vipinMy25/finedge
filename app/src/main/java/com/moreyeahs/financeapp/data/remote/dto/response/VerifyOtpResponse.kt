package com.moreyeahs.financeapp.data.remote.dto.response

data class VerifyOtpResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean
) {
    data class Data(
        val _id: String,
        val fullName: String,
        val Email: String,
        val password: String,
        val confirm_password: String,
        val Mobile_No: String,
        val is_verified: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val __v: Int
    )
}