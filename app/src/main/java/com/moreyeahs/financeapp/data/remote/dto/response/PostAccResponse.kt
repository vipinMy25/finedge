package com.moreyeahs.financeapp.data.remote.dto.response

data class PostAccResponse(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val user_Id: String,
        val _id: String,
        val bankName: String,
        val accountNumber: String,
        val accountType: String,
        val userAccountName: String,
        val logoUrl: String,
        val is_default: Boolean,
        val __v: Int
    )
}