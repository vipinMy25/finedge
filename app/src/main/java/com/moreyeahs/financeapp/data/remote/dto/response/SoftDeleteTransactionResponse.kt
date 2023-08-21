package com.moreyeahs.financeapp.data.remote.dto.response

data class SoftDeleteTransactionResponse(
    val deleteSms: DeleteSms,
    val message: String,
    val status: Boolean
) {
    data class DeleteSms(
        val __v: Int,
        val _id: String,
        val accountNumber: String,
        val accountType: String,
        val amount: String,
        val createdAt: String,
        val is_deleted: Boolean,
        val sender: String,
        val smsTime: Long,
        val text: String,
        val transactionType: String,
        val updatedAt: String,
        val upiID: String,
        val userId: String
    )
}