package com.moreyeahs.financeapp.data.remote.dto.response

data class UpdateAccResponse(
    val getaccId: GetaccId,
    val message: String,
    val status: Boolean
) {
    data class GetaccId(
        val __v: Int,
        val _id: String,
        val accountNumber: String,
        val accountType: String,
        val bankName: String,
        val is_default: Boolean,
        val logoUrl: String,
        val userAccountName: String,
        val user_Id: String
    )
}