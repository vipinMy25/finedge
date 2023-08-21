package com.moreyeahs.financeapp.data.remote.dto.request

data class TransactionUpdateRequest(
    val _id: String,
    val sms: String,
    val transactionType: String,
    val amount: String,
    val bankName: String,
    val accountNumber: String,
    val accountType: String,
    val upiID: String,
    val timestamp: String
)