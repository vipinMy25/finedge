package com.moreyeahs.financeapp.domain.repository

import android.content.Context
import com.moreyeahs.financeapp.data.local.FinanceDatabase
import com.moreyeahs.financeapp.data.local.dao.FinanceDao
import com.moreyeahs.financeapp.data.remote.api.FinanceApi
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
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.isNetworkConnected
import com.moreyeahs.financeapp.util.toAccountEntity
import com.moreyeahs.financeapp.util.toAccountModel
import com.moreyeahs.financeapp.util.toTransactionModel
import com.moreyeahs.financeapp.util.toTransactionEntity
import org.json.JSONObject
import javax.inject.Inject

class FinanceRepoImpl @Inject constructor(
    private val context: Context,
    private val finDb: FinanceDatabase,
    private val financeDao: FinanceDao,
    private val financeApi: FinanceApi,
    private val preferencesManager: PreferencesManager
) : FinanceRepo {

    override suspend fun insertTransaction(transactionModel: TransactionModel) {
        financeDao.insertTransaction(transactionModel.toTransactionEntity())
    }

    override suspend fun getAllTransactionFromDb(): List<TransactionModel> {
        return financeDao.getAllTransaction().map {
            it.toTransactionModel()
        }
    }

    override suspend fun deleteTransaction(transactionModel: TransactionModel) {
        financeDao.deleteTransaction(transactionModel.id)
    }

    override suspend fun insertAccount(bankItem: AccountModel) {
        financeDao.insertAccount(bankItem = bankItem.toAccountEntity())
    }

    override suspend fun getAllAccountFromDb(): List<AccountModel> {
        return financeDao.getAllAccount().map {
            it.toAccountModel()
        }
    }

    override suspend fun updateAccountDefault(id: String, isDefault: Boolean) {
        financeDao.updateAccountDefault(id, isDefault)
    }

    override suspend fun deleteAll() {
        finDb.clearAllTables()
    }

    override suspend fun postTransactions(postTransactionRequest: PostTransactionRequest): NetworkingResult<PostTransactionResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.postTransactions(accessToken = "Bearer $token", postTransactionRequest = postTransactionRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun addTransaction(addTransactionRequest: AddTransactionRequest): NetworkingResult<AddTransactionResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.addTransaction(accessToken = "Bearer $token", addTransactionRequest = addTransactionRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun getAllUsersTransactions(): NetworkingResult<PostTransactionResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.getAllUsersTransactions(accessToken = "Bearer $token")
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun getUserAllTransactions(id: String): NetworkingResult<PostTransactionResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.getUserAllTransactions(accessToken = "Bearer $token", id = id)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun updateTransaction(transactionUpdateRequest: TransactionUpdateRequest): NetworkingResult<TransactionUpdateResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.updateTransaction(accessToken = "Bearer $token", transactionUpdateRequest = transactionUpdateRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun softDeleteTransaction(id: String): NetworkingResult<SoftDeleteTransactionResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.softDeleteTransaction(accessToken = "Bearer $token", id = id)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun postAccounts(postAccRequest: PostAccRequest): NetworkingResult<PostAccResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.postAccounts(accessToken = "Bearer $token", postAccRequest = postAccRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun getAccounts(id: String): NetworkingResult<PostAccResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.getAllAccounts(accessToken = "Bearer $token", id = id)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun updateAccount(updateAccRequest: UpdateAccRequest): NetworkingResult<UpdateAccResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val userId = preferencesManager.getString(Constants.USER_ID)
                val response = financeApi.updateAccount(accessToken = "Bearer $token", updateAccRequest = updateAccRequest.copy(user_Id = userId ?: ""))
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun postFeedback(feedbackRequest: FeedbackRequest): NetworkingResult<FeedbackResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = financeApi.postFeedback(
                    accessToken = "Bearer $token", feedbackRequest = feedbackRequest
                )
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

}