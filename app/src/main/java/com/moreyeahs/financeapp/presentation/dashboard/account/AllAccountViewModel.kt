package com.moreyeahs.financeapp.presentation.dashboard.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAccountViewModel @Inject constructor(
    val financeRepo: FinanceRepo,
    val preferencesManager: PreferencesManager
) : ViewModel() {

    private var allAccountList = listOf<AccountModel>()

    fun getAllAccountsFromDb() = viewModelScope.launch {
        allAccountList = financeRepo.getAllAccountFromDb()
    }

    fun getCategorisedList(): HashMap<String, List<AccountModel>> {
        val typeList = allAccountList.map { it.accountType }
        val uniqueTypes = typeList.toSet().toMutableList()

        val categorisedMap = HashMap<String, List<AccountModel>>()

        uniqueTypes.forEach { type ->
            val filterList = allAccountList.filter { allBankItemModel ->
                allBankItemModel.accountType.equals(type, true)
            }
            if (type.isNotEmpty() && !type.equals("Cash", true)) {
                categorisedMap[type] = filterList
            }
        }
        return categorisedMap
    }

}