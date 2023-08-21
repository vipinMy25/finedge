package com.moreyeahs.financeapp.data.remote.dto.response

data class AddTransactionResponse(
    val `data`: Data,
    val status: Boolean
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val accountNumber: String,
        val accountType: String,
        val amount: String,
        val bankName: String,
        val createdAt: String,
        val logoUrl: String,
        val sender: String,
        val sms: String,
        val transactionType: String,
        val updatedAt: String,
        val upiID: String,
        val userId: String,
        val timestamp: String
    )
}