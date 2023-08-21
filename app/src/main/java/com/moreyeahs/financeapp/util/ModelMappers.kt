package com.moreyeahs.financeapp.util

import com.moreyeahs.financeapp.data.local.entity.AccountEntity
import com.moreyeahs.financeapp.data.local.entity.TransactionEntity
import com.moreyeahs.financeapp.data.remote.dto.request.AddTransactionRequest
import com.moreyeahs.financeapp.data.remote.dto.request.TransactionUpdateRequest
import com.moreyeahs.financeapp.data.remote.dto.response.AddTransactionResponse
import com.moreyeahs.financeapp.data.remote.dto.response.PostAccResponse
import com.moreyeahs.financeapp.data.remote.dto.response.PostTransactionResponse
import com.moreyeahs.financeapp.data.remote.dto.response.TransactionUpdateResponse
import com.moreyeahs.financeapp.data.remote.dto.response.UpdateAccResponse
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.model.TransactionModel

fun PostTransactionResponse.Transaction.toTransactionModel(): TransactionModel {
    return TransactionModel(
        id = _id,
        timestamp = timestamp,
        sms = sms,
        sender = sender,
        transactionType = transactionType,
        amount = amount,
        upiID = upiID,
        accountType = accountType,
        accountNumber = accountNumber,
        bankName = bankName,
        bankLogoUrl = logoUrl
    )
}

fun TransactionModel.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        timestamp = timestamp,
        sms = sms,
        sender = sender,
        transactionType = transactionType,
        amount = amount,
        upiID = upiID,
        accountType = accountType,
        accountNumber = accountNumber,
        bankName = bankName,
        bankLogoUrl = bankLogoUrl
    )
}

fun TransactionEntity.toTransactionModel(): TransactionModel {
    return TransactionModel(
        id = id,
        timestamp = timestamp,
        sms = sms,
        sender = sender,
        transactionType = transactionType,
        amount = amount,
        upiID = upiID,
        accountType = accountType,
        accountNumber = accountNumber,
        bankName = bankName,
        bankLogoUrl = bankLogoUrl
    )
}

fun TransactionModel.toAddTransactionRequest(userId: String): AddTransactionRequest {
    return AddTransactionRequest(
        timestamp = timestamp,
        accountNumber = accountNumber,
        accountType = accountType,
        amount = amount,
        bankName = bankName,
        sender = sender,
        sms = sms,
        transactionType = transactionType,
        upiID = upiID,
        userId = userId
    )
}

fun AddTransactionResponse.Data.toTransactionModel(): TransactionModel {
    return TransactionModel(
        id = _id,
        timestamp = timestamp,
        amount = amount,
        upiID = upiID,
        sender = sender,
        sms = sms,
        transactionType = transactionType,
        accountType = accountType,
        bankName = bankName,
        accountNumber = accountNumber,
        bankLogoUrl = logoUrl
    )
}

fun TransactionModel.toTransactionUpdateRequest(): TransactionUpdateRequest {
    return TransactionUpdateRequest(
        _id = id,
        accountNumber = accountNumber,
        accountType = accountType,
        amount = amount,
        bankName = bankName,
        timestamp = timestamp,
        sms = sms,
        transactionType = transactionType,
        upiID = upiID
    )
}

fun TransactionUpdateResponse.Data.toTransactionModel(): TransactionModel {
    return TransactionModel(
        id = _id,
        timestamp = timestamp,
        sms = sms,
        sender = sender,
        transactionType = transactionType,
        amount = amount,
        upiID = upiID,
        accountType = accountType,
        accountNumber = accountNumber,
        bankName = bankName,
        bankLogoUrl = logoUrl
    )
}

fun PostAccResponse.Data.toAccountModel(): AccountModel {
    return AccountModel(
        id = _id,
        bankName = bankName,
        accountNumber = accountNumber,
        accountType = accountType,
        userAccountName = userAccountName,
        bankLogoUrl = logoUrl,
        isDefault = is_default
    )
}

fun AccountModel.toAccountEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        bankName = bankName,
        accountNumber = accountNumber,
        accountType = accountType,
        userAccountName = userAccountName,
        isDefault = isDefault,
        bankLogoUrl = bankLogoUrl
    )
}

fun AccountEntity.toAccountModel(): AccountModel {
    return AccountModel(
        bankName = bankName,
        accountNumber = accountNumber,
        accountType = accountType,
        isDefault = isDefault,
        userAccountName = userAccountName,
        id = id,
        bankLogoUrl = bankLogoUrl
    )
}

fun UpdateAccResponse.GetaccId.toAccountModel(): AccountModel {
    return AccountModel(
        id = _id,
        bankName = bankName,
        accountNumber = accountNumber,
        accountType = accountType,
        userAccountName = userAccountName,
        bankLogoUrl = logoUrl,
        isDefault = is_default
    )
}