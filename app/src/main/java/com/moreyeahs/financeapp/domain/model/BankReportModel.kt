package com.moreyeahs.financeapp.domain.model

data class BankReportModel(
    var smsTime: String,
    val bankName: String,
    var isSelected: Boolean = false,
    var accountNo: String,
    var userAccountName: String = "",
    var amount: Double?,
    var transactionType: String,
    val bankLogoUrl: String
)
