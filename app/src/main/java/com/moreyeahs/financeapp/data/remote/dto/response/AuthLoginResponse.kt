package com.moreyeahs.financeapp.data.remote.dto.response

data class AuthLoginResponse(
    val `data`: Data,
    val token: String,
    val time: Time?
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

    data class Time(
        val lastSyncSmsTime: Long?
    )
}