package com.moreyeahs.financeapp.data.remote.dto.response

data class EditProfileGuestResponse(
    val `data`: Data,
    val message: String
) {
    data class Data(
        val _id: String,
        val fullName: String,
        val deviceId: String,
        val is_verified: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val __v: Int,
        val Email: String,
        val Mobile_No: String
    )
}