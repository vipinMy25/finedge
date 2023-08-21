package com.moreyeahs.financeapp.domain.model

data class AccountModel(
    val id: String,
    val bankName: String,
    val accountNumber: String,
    val accountType: String,
    val userAccountName: String,
    val bankLogoUrl: String,
    var isDefault: Boolean
)