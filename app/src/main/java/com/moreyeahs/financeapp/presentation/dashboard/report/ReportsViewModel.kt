package com.moreyeahs.financeapp.presentation.dashboard.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.model.BankFilterModel
import com.moreyeahs.financeapp.domain.model.BankReportModel
import com.moreyeahs.financeapp.domain.model.TransactionModel
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val financeRepo: FinanceRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _recentTransactionListModel = MutableLiveData<List<TransactionModel>>(listOf())
    var recentTransactionListModel: LiveData<List<TransactionModel>> = _recentTransactionListModel

    private val _allTransactionListModel = MutableLiveData<List<TransactionModel>>(listOf())
    var allTransactionListModel: LiveData<List<TransactionModel>> = _allTransactionListModel

    private var lastMessageInsertedTime = "0"

    private val _allBankList = MutableLiveData<List<BankFilterModel>>(listOf())
    var allBankList: LiveData<List<BankFilterModel>> = _allBankList

    private val _debitAmount = MutableLiveData<Double>(0.00)
    var debitAmount: LiveData<Double> = _debitAmount

    private val _creditAmount = MutableLiveData<Double>(0.00)
    var creditAmount: LiveData<Double> = _creditAmount

    private val _dailyAvgExpense = MutableLiveData<Double>(0.00)
    var dailyAvgExpense: LiveData<Double> = _dailyAvgExpense

    var allBankItemsFromDb: List<AccountModel> = listOf()

    init {
        getAllBank()
    }

    fun getAllBank() {
        viewModelScope.launch {
            allBankItemsFromDb = smsRepo.getAllBankItemsFromDb(true)
        }
    }

    fun syncDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val allSmsFromDbJobUpdated = getAllSmsFromDb()
            allSmsFromDbJobUpdated.join()
        }
    }

    fun getAllSmsFromDb() = viewModelScope.launch(Dispatchers.IO) {
        val allSmsFromDb = financeRepo.getAllTransactionFromDb()
        _allTransactionListModel.postValue(allSmsFromDb)

        if (allSmsFromDb.isNotEmpty()) {
            val maxTimeSms = allSmsFromDb.maxBy { it.timestamp.toLong() }
            maxTimeSms.let {
                lastMessageInsertedTime = it.timestamp
            }
        }

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val smsList = allSmsFromDb.filter {
            println("timeStamp ${cal.time.time}")
            it.timestamp.toLong() >= cal.time.time
        }
        _recentTransactionListModel.postValue(smsList)

        val allBank = getAllBank(smsList)
        allBank.join()

        val incomeExpense = getIncomeExpense(smsList)
        incomeExpense.join()

        val calendar = Calendar.getInstance()
        val endFilterDate = calendar.time.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startFilterDate = calendar.time.time
        val dailyAvgJob = getDailyAvgExpense(smsList, startFilterDate, endFilterDate)
        dailyAvgJob.join()

        allBankItemsFromDb = financeRepo.getAllAccountFromDb()
    }


    private fun getAllBank(transactionModelList: List<TransactionModel>) = viewModelScope.launch(Dispatchers.IO) {
        val list: ArrayList<BankFilterModel> = arrayListOf()
        transactionModelList.forEach {
            list.add(
                BankFilterModel(
                    bankName = it.bankName,
                    accountNo = it.accountNumber
                )
            )
        }

        val newList = list.toSet().toMutableList()
        val allBankItemsFromDb = financeRepo.getAllAccountFromDb()
        val finalList = newList.map { bankItem ->
            allBankItemsFromDb.forEach { accountItem ->
                if (bankItem.bankName.equals(accountItem.bankName, true) && bankItem.accountNo.equals(accountItem.accountNumber, true)) {
                    if (accountItem.userAccountName.isNotEmpty()) {
                        bankItem.userAccountName = accountItem.userAccountName
                    }
                }
            }
            bankItem
        }
        val listToShow = finalList.filter {
            it.accountNo.length > 2
        }

        _allBankList.postValue(listToShow)
    }

    fun getIncomeExpense(transactionModelList: List<TransactionModel>) = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (transactionModelList.isNotEmpty()) {
                var creditAmo: Double = 0.00
                var debitAmo: Double = 0.00
                transactionModelList.forEach {
                    if (it.amount.length > 1) {
                        if (it.transactionType.equals("Credit", ignoreCase = true)) {
                            creditAmo += it.amount.replace(",", "").toDouble()
                        } else {
                            debitAmo += it.amount.replace(",", "").toDouble()
                        }
                    }
                }
                _creditAmount.postValue(creditAmo)
                _debitAmount.postValue(debitAmo)
            }
        } catch (x: Exception) {
            x.printStackTrace()
        }
    }

    fun getIncomeExpenseByReport(smsList: List<BankReportModel>) = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (smsList.isNotEmpty()) {
                var creditAmo: Double = 0.00
                var debitAmo: Double = 0.00
                smsList.forEach {
                    it.amount?.let { amount ->
                        if (amount > 0) {
                            if (it.transactionType.equals("Credit", ignoreCase = true)) {
                                creditAmo += amount
                            } else {
                                debitAmo += amount
                            }
                        }
                    }

                }
                _creditAmount.postValue(creditAmo)
                _debitAmount.postValue(debitAmo)
            }
        } catch (x: Exception) {
            x.printStackTrace()
        }
    }

    fun getDailyAvgExpense(transactionModelList: List<TransactionModel>, startDate: Long, endDate: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (transactionModelList.isNotEmpty()) {
                val timeDiff = endDate - startDate
                val days = TimeUnit.MILLISECONDS.toDays(timeDiff).toInt()

                val expenseList = transactionModelList.filter {
                    it.transactionType.equals("Debit", true)
                }

                var totalExpense = 0.0
                expenseList.forEach {
                    totalExpense += it.amount.replace(",", "").toDouble()
                }
                val avgExpense = totalExpense / (days + 1)
                _dailyAvgExpense.postValue(avgExpense)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}