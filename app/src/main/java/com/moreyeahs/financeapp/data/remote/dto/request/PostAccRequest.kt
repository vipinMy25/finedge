package com.moreyeahs.financeapp.data.remote.dto.request

data class PostAccRequest(
    val _id: String,
    val sms: List<Sms>
) {
    data class Sms(
        val bankName: String,
        val accountNumber: String,
        val accountType: String,
        val userAccountName: String
    )
}
