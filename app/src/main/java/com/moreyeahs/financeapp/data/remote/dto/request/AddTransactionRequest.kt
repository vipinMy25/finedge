package com.moreyeahs.financeapp.data.remote.dto.request

data class AddTransactionRequest(
    val timestamp: String,
    val accountNumber: String,
    val accountType: String,
    val amount: String,
    val bankName: String,
    val sender: String,
    val sms: String,
    val transactionType: String,
    val upiID: String,
    val userId: String
)