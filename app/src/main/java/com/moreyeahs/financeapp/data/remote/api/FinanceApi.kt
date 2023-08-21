package com.moreyeahs.financeapp.data.remote.api

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface FinanceApi {

    @POST("/sms-data")
    suspend fun postTransactions(
        @Header("Authorization") accessToken: String,
        @Body postTransactionRequest: PostTransactionRequest
    ): Response<PostTransactionResponse>

    @POST("/addtransaction")
    suspend fun addTransaction(
        @Header("Authorization") accessToken: String,
        @Body addTransactionRequest: AddTransactionRequest
    ): Response<AddTransactionResponse>

    @GET("/smsgetdata")
    suspend fun getUserAllTransactions(
        @Header("Authorization") accessToken: String,
        @Query("userId") id: String
    ): Response<PostTransactionResponse>

    @GET("/getallsmsdata")
    suspend fun getAllUsersTransactions(
        @Header("Authorization") accessToken: String,
    ): Response<PostTransactionResponse>

    @PUT("/smsUpdate")
    suspend fun updateTransaction(
        @Header("Authorization") accessToken: String,
        @Body transactionUpdateRequest: TransactionUpdateRequest
    ): Response<TransactionUpdateResponse>

    @DELETE("/softDeleteSms")
    suspend fun softDeleteTransaction(
        @Header("Authorization") accessToken: String,
        @Query("_id") id: String
    ): Response<SoftDeleteTransactionResponse>

    @POST("/postAcc")
    suspend fun postAccounts(
        @Header("Authorization") accessToken: String,
        @Body postAccRequest: PostAccRequest
    ): Response<PostAccResponse>

    @GET("/getAcc")
    suspend fun getAllAccounts(
        @Header("Authorization") accessToken: String,
        @Query("_id") id: String
    ): Response<PostAccResponse>

    @PUT("/updateAcc")
    suspend fun updateAccount(
        @Header("Authorization") accessToken: String,
        @Body updateAccRequest: UpdateAccRequest
    ): Response<UpdateAccResponse>

    @POST("/userFeedBack")
    suspend fun postFeedback(
        @Header("Authorization") accessToken: String,
        @Body feedbackRequest: FeedbackRequest
    ): Response<FeedbackResponse>

}