package com.moreyeahs.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val bankName: String,
    val accountNumber: String,
    val accountType: String,
    val userAccountName: String,
    val bankLogoUrl: String,
    val isDefault: Boolean
)