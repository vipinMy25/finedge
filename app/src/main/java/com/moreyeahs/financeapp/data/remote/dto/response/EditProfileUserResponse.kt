package com.moreyeahs.financeapp.data.remote.dto.response

data class EditProfileUserResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean
) {
    data class Data(
        val Email: String,
        val Mobile_No: String,
        val __v: Int,
        val _id: String,
        val confirm_password: String,
        val createdAt: String,
        val fullName: String,
        val is_verified: Boolean,
        val password: String,
        val updatedAt: String
    )
}