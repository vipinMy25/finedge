package com.moreyeahs.financeapp.presentation.dashboard

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.PostTransactionRequest
import com.moreyeahs.financeapp.domain.model.TransactionModel
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.Constants.LAST_SYNC_MESSAGE_TIME
import com.moreyeahs.financeapp.util.Constants.USER_ID
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.getLatestDeviceSms
import com.moreyeahs.financeapp.util.logoutUser
import com.moreyeahs.financeapp.util.toAccountModel
import com.moreyeahs.financeapp.util.toTransactionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val financeRepo: FinanceRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun syncDatabase(context: Context, activity: Activity?) = viewModelScope.launch(Dispatchers.IO) {

        var lastSyncMessageTime = preferencesManager.getString(LAST_SYNC_MESSAGE_TIME)
        if (lastSyncMessageTime.isNullOrEmpty()) {
            lastSyncMessageTime = "0"
        }
        val smsDataJob = smsData(getLatestDeviceSms(context, lastSyncMessageTime.toLong()), context, activity)
        smsDataJob.join()

        val getSmsDataJob = getUserAllSmsData(context, activity)
        getSmsDataJob.join()

        val getAccountJob = getAllAccounts(context, activity)
        getAccountJob.join()
    }

    private fun smsData(smsList: List<PostTransactionRequest.Transaction>, context: Context, activity: Activity?) = viewModelScope.launch {
        val userId = preferencesManager.getString(USER_ID)
        val lastSyncMessageTime = preferencesManager.getString(LAST_SYNC_MESSAGE_TIME)
        var allSms = smsList
        try {
            if (!lastSyncMessageTime.isNullOrEmpty()) {
                allSms = allSms.filter {
                    it.timestamp.toLong() > lastSyncMessageTime.toLong()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (allSms.isNotEmpty()) {
            val maxTimeSms = allSms.maxBy { it.timestamp.toLong() }

            val chunked = allSms.chunked(50)
            chunked.forEach { subSmsList ->
                when (val result = financeRepo.postTransactions(PostTransactionRequest(transactions = subSmsList, userId = userId ?: ""))) {
                    is NetworkingResult.Error -> {
                        Log.d("MainViewModel", "smsData: Error :: ${result.message}")
                        if (result.message.equals("Your token is invalid or expired", true)) {
                            logoutUser(preferencesManager, financeRepo, context, activity)
                        }
                    }

                    is NetworkingResult.Success -> {
                        Log.d("MainViewModel", "smsData: Success :: ${result.data}")
                        preferencesManager.putString(LAST_SYNC_MESSAGE_TIME, maxTimeSms.timestamp)
                    }
                }
            }
        }
    }

    private fun getUserAllSmsData(context: Context, activity: Activity?) = viewModelScope.launch {
        when (val result = financeRepo.getUserAllTransactions(preferencesManager.getString(USER_ID) ?: "")) {
            is NetworkingResult.Error -> {
                Log.d("HomeViewModel", "getAllSmsData: Error :: ${result.message}")
                if (result.message.equals("Your token is invalid or expired", true)) {
                    logoutUser(preferencesManager, financeRepo, context, activity)
                }
            }

            is NetworkingResult.Success -> {
                Log.d("MainViewModel", "getAllSmsData: Success :: ${result.data}")
                result.data?.data?.forEach {
                    val insertSmsJob = insertSms(transactionModel = it.toTransactionModel())
                    insertSmsJob.join()
                }
            }
        }
    }

    private fun insertSms(transactionModel: TransactionModel) = viewModelScope.launch(Dispatchers.IO) {
        financeRepo.insertTransaction(transactionModel)
    }

    private fun getAllAccounts(context: Context, activity: Activity?) = viewModelScope.launch(Dispatchers.IO) {
        val userId = preferencesManager.getString(USER_ID)
        when (val result = financeRepo.getAccounts(userId ?: "")) {
            is NetworkingResult.Error -> {
                Log.d("HomeViewModel", "postAccount: Error :: ${result.message}")
                if (result.message.equals("Your token is invalid or expired", true)) {
                    logoutUser(preferencesManager, financeRepo, context, activity)
                }
            }

            is NetworkingResult.Success -> {
                Log.d("HomeViewModel", "postAccount: Success :: ${result.data}")
                result.data?.data?.let { resAccList ->
                    resAccList.forEach { acc ->
                        financeRepo.insertAccount(acc.toAccountModel())
                    }
                }
            }
        }
    }

    private fun getAllSmsData(context: Context, activity: Activity?) = viewModelScope.launch {
        when (val result = financeRepo.getAllUsersTransactions()) {
            is NetworkingResult.Error -> {
                Log.d("HomeViewModel", "getAllSmsData: Error :: ${result.message}")
                if (result.message.equals("Your token is invalid or expired", true)) {
                    logoutUser(preferencesManager, financeRepo, context, activity)
                }
            }

            is NetworkingResult.Success -> {
                result.data?.data?.forEach {
                    val insertSmsJob = insertSms(transactionModel = it.toTransactionModel())
                    insertSmsJob.join()
                }
            }
        }
    }

}