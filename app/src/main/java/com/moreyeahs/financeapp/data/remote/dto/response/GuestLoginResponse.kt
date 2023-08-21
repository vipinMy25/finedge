package com.moreyeahs.financeapp.data.remote.dto.response

data class GuestLoginResponse(
    val `data`: Data,
    val message: String?,
    val token: String,
    val Maxtime: MaxTime?
) {
    data class Data(
        val _id: String,
        val fullName: String,
        val deviceId: String,
        val is_verified: Boolean,
        val Email: String,
        val Mobile_No: String,
        val createdAt: String,
        val updatedAt: String,
        val __v: Int
    )

    data class MaxTime(
        val lastSyncSmsTime: Long?
    )
}