package com.moreyeahs.financeapp.domain.model

data class TransactionModel(
    var id: String,
    val timestamp: String,
    val amount: String,
    val upiID: String,
    val sender: String,
    val sms: String,
    val transactionType: String,
    val accountType: String,
    val bankName: String,
    val accountNumber: String,
    val bankLogoUrl: String
)
