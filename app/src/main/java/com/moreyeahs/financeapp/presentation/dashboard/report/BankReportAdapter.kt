package com.moreyeahs.financeapp.presentation.dashboard.report

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.BankNameItemLayoutBinding
import com.moreyeahs.financeapp.domain.model.BankReportModel
import com.moreyeahs.financeapp.util.Constants
import java.text.DecimalFormat

class BankReportAdapter(var context: Context, var bankReportList: List<BankReportModel>) :
    RecyclerView.Adapter<BankReportAdapter.BankReportViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BankReportViewHolder {
        return BankReportViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.bank_name_item_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BankReportAdapter.BankReportViewHolder, position: Int) {
        val data = bankReportList[position]
        if (data.bankName.isNotEmpty()) {
            holder.bind(data, context)
        }

    }

    override fun getItemCount(): Int {
        return bankReportList.size
    }

    class BankReportViewHolder(private var binding: BankNameItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bankItem: BankReportModel, context: Context) {
            binding.tvBankName.text = bankItem.bankName
            if (bankItem.bankName.isEmpty()) {
                binding.tvBankName.text = "UPI"
            } else {
                binding.tvBankName.text = bankItem.bankName
            }
            val df = DecimalFormat("#.##")

            binding.tvBankAccountNumber.text = bankItem.accountNo
            binding.tvExpenseAmount.text = "₹${df.format(bankItem.amount)}"
            Glide.with(context)
                .load("${Constants.BASE_URL}/${bankItem.bankLogoUrl}")
                .fitCenter()
                .placeholder(R.drawable.transaction_item_icon)
                .into(binding.ivBankLogo)
        }
    }

}