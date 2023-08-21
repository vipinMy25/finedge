package com.moreyeahs.financeapp.presentation.dashboard.account

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.data.remote.dto.request.UpdateAccRequest
import com.moreyeahs.financeapp.databinding.BankItemLayoutBinding
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.logoutUser
import com.moreyeahs.financeapp.util.toAccountModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountItemAdapter(
    val context: Context,
    val activity: Activity?,
    var bankList: List<AccountModel>,
    val financeRepo: FinanceRepo,
    val preferencesManager: PreferencesManager,
    var onNotify: () -> Unit
) : RecyclerView.Adapter<AccountItemAdapter.AccountItemViewHolder>() {

    class AccountItemViewHolder(private val binding: BankItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            bankItem: AccountModel,
            context: Context,
            activity: Activity?,
            financeRepo: FinanceRepo,
            preferencesManager: PreferencesManager,
            onNotify: () -> Unit
        ) {
            binding.tvBankName.text = bankItem.userAccountName.ifEmpty { bankItem.bankName }
            binding.tvAccountNumber.text = bankItem.accountNumber

            Glide.with(context)
                .load("${Constants.BASE_URL}/${bankItem.bankLogoUrl}")
                .fitCenter()
                .placeholder(R.drawable.transaction_item_icon)
                .into(binding.ivBankLogo)

            if (bankItem.isDefault) {
                binding.tvDefaultAccount.visibility = View.VISIBLE
            } else {
                binding.tvDefaultAccount.visibility = View.GONE
            }

            binding.ivEdit.setOnClickListener {
                showEditAccountDialog(context, activity, bankItem, financeRepo, preferencesManager) {
                    onNotify.invoke()
                }
            }
        }

        private fun showEditAccountDialog(
            context: Context,
            activity: Activity?,
            bankItem: AccountModel,
            financeRepo: FinanceRepo,
            preferencesManager: PreferencesManager,
            onNotify: () -> Unit
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_edit_account)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)

            val tvDescription = dialog.findViewById(R.id.tv_description_edit_account) as TextView
            val etAccountName = dialog.findViewById(R.id.et_account_name) as EditText
            val btnApply = dialog.findViewById(R.id.btn_save_edit_account) as Button
            val btnCancel = dialog.findViewById(R.id.btn_cancel_edit_account) as Button
            val scDefaultAcc = dialog.findViewById(R.id.sc_default_acc) as SwitchCompat

            tvDescription.text = "Please enter your name for account ${bankItem.bankName} (${bankItem.accountNumber})"
            if (bankItem.isDefault) {
                scDefaultAcc.isChecked = true
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnApply.setOnClickListener {
                val userAccountName = etAccountName.text.toString().trim()
                CoroutineScope(Dispatchers.Main).launch {
                    if (!userAccountName.equals(bankItem.userAccountName, false) || scDefaultAcc.isChecked != bankItem.isDefault) {
                        val result = financeRepo.updateAccount(
                            UpdateAccRequest(
                                user_Id = "",
                                _id = bankItem.id,
                                is_default = scDefaultAcc.isChecked,
                                userAccountName = userAccountName
                            )
                        )
                        when (result) {
                            is NetworkingResult.Error -> {
                                Log.d("AccountItemAdapter", "showEditAccountDialog: Error :: ${result.message}")
                                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                if (result.message.equals("Your token is invalid or expired", true)) {
                                    logoutUser(preferencesManager, financeRepo, context, activity)
                                }
                            }

                            is NetworkingResult.Success -> {
                                Log.d("AccountItemAdapter", "showEditAccountDialog: Success :: ${result.data}")
                                val allBankItemsFromDb = financeRepo.getAllAccountFromDb()
                                val defaultAccounts = allBankItemsFromDb.filter { it.isDefault }
                                defaultAccounts.forEach { defaultAcc ->
                                    financeRepo.updateAccountDefault(defaultAcc.id, false)
                                }
                                result.data?.getaccId?.let {
                                    financeRepo.insertAccount(it.toAccountModel())
                                }
                                binding.tvBankName.text = userAccountName
                                onNotify.invoke()
                            }
                        }
                    }

                    dialog.dismiss()
                }
            }

            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountItemViewHolder {
        return AccountItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.bank_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    override fun onBindViewHolder(holder: AccountItemViewHolder, position: Int) {
        holder.bind(bankList[position], context, activity, financeRepo, preferencesManager) {
            onNotify.invoke()
        }
    }

}