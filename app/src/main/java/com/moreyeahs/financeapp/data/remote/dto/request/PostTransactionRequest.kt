package com.moreyeahs.financeapp.data.remote.dto.request

data class PostTransactionRequest(
    val transactions: List<Transaction>,
    val userId: String
) {
    data class Transaction(
        val sender: String,
        val text: String,
        val timestamp: String
    )
}