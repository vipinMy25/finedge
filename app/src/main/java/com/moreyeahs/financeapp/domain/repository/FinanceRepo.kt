package com.moreyeahs.financeapp.domain.repository

import com.moreyeahs.financeapp.data.remote.dto.request.AddTransactionRequest
import com.moreyeahs.financeapp.data.remote.dto.request.FeedbackRequest
import com.moreyeahs.financeapp.data.remote.dto.request.PostAccRequest
import com.moreyeahs.financeapp.data.remote.dto.request.PostTransactionRequest
import com.moreyeahs.financeapp.data.remote.dto.request.TransactionUpdateRequest
import com.moreyeahs.financeapp.data.remote.dto.request.UpdateAccRequest
import com.moreyeahs.financeapp.data.remote.dto.response.AddTransactionResponse
import com.moreyeahs.financeapp.data.remote.dto.response.FeedbackResponse
import com.moreyeahs.financeapp.data.remote.dto.response.PostAccResponse
import com.moreyeahs.financeapp.data.remote.dto.response.PostTransactionResponse
import com.moreyeahs.financeapp.data.remote.dto.response.TransactionUpdateResponse
import com.moreyeahs.financeapp.data.remote.dto.response.SoftDeleteTransactionResponse
import com.moreyeahs.financeapp.data.remote.dto.response.UpdateAccResponse
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.model.TransactionModel
import com.moreyeahs.financeapp.util.NetworkingResult

interface FinanceRepo {
    suspend fun insertTransaction(transactionModel: TransactionModel)
    suspend fun getAllTransactionFromDb(): List<TransactionModel>
    suspend fun deleteTransaction(transactionModel: TransactionModel)
    suspend fun insertAccount(bankItem: AccountModel)
    suspend fun getAllAccountFromDb(): List<AccountModel>
    suspend fun updateAccountDefault(id: String, isDefault: Boolean)
    suspend fun deleteAll()

    suspend fun postTransactions(postTransactionRequest: PostTransactionRequest): NetworkingResult<PostTransactionResponse>

    suspend fun addTransaction(addTransactionRequest: AddTransactionRequest): NetworkingResult<AddTransactionResponse>

    suspend fun getAllUsersTransactions(): NetworkingResult<PostTransactionResponse>

    suspend fun getUserAllTransactions(id: String): NetworkingResult<PostTransactionResponse>

    suspend fun updateTransaction(transactionUpdateRequest: TransactionUpdateRequest): NetworkingResult<TransactionUpdateResponse>

    suspend fun softDeleteTransaction(id: String): NetworkingResult<SoftDeleteTransactionResponse>

    suspend fun postAccounts(postAccRequest: PostAccRequest): NetworkingResult<PostAccResponse>

    suspend fun getAccounts(id: String): NetworkingResult<PostAccResponse>

    suspend fun updateAccount(updateAccRequest: UpdateAccRequest): NetworkingResult<UpdateAccResponse>

    suspend fun postFeedback(feedbackRequest: FeedbackRequest): NetworkingResult<FeedbackResponse>

}