package com.moreyeahs.financeapp.domain.model

data class BankFilterModel(
    val bankName: String,
    var isSelected: Boolean = false,
    var accountNo: String,
    var userAccountName: String = ""
)
