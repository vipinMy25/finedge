package com.moreyeahs.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = false)
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
