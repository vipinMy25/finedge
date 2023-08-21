package com.moreyeahs.financeapp.data.remote.dto.response

data class PostTransactionResponse(
    val status: Boolean,
    val data: List<Transaction>
) {
    data class Transaction(
        val accountNumber: String,
        val accountType: String,
        val amount: String,
        val bankName: String,
        val date: String,
        val logoUrl: String,
        val sender: String,
        val sms: String,
        val timestamp: String,
        val transactionType: String,
        val upiID: String,
        val _id: String
    )
}