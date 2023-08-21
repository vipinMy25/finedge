package com.moreyeahs.financeapp.data.remote.dto.response

data class TransactionUpdateResponse(
    val data: Data,
    val message: String,
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
        val date: String,
        val logoUrl: String,
        val sender: String,
        val sms: String,
        val timestamp: String,
        val transactionType: String,
        val updatedAt: String,
        val upiID: String,
        val userId: String
    )
}