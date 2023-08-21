package com.moreyeahs.financeapp.data.remote.dto.request

data class EditProfileGuestRequest(
    val fullName: String,
    val deviceId: String,
    val Email: String,
    val mobileNumber: String
)