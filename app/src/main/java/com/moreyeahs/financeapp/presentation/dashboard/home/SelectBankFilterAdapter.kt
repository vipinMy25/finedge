package com.moreyeahs.financeapp.presentation.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.LayoutFilterBankItemBinding
import com.moreyeahs.financeapp.domain.model.BankFilterModel
import com.moreyeahs.financeapp.util.toPx

class SelectBankFilterAdapter(var context: Context) : RecyclerView.Adapter<SelectBankFilterAdapter.BankItemViewHolder>() {

    class BankItemViewHolder(private val binding: LayoutFilterBankItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bank: BankFilterModel, context: Context) {
            binding.tvBankName.text = bank.userAccountName.ifEmpty { bank.bankName }
            binding.tvBankAccountNumber.text = bank.accountNo

            if (bank.isSelected) {
                binding.rlFilterBankItem.background = context.resources.getDrawable(R.drawable.rounded_sides_blue_background, null)
                binding.tvBankName.setTextColor(context.resources.getColor(R.color.white, null))
                binding.tvBankAccountNumber.setTextColor(context.resources.getColor(R.color.white, null))
                binding.rlFilterBankItem.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
            } else {
                binding.rlFilterBankItem.background = context.resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
                binding.tvBankName.setTextColor(context.resources.getColor(R.color.text_blue, null))
                binding.tvBankAccountNumber.setTextColor(context.resources.getColor(R.color.text_grey, null))
                binding.rlFilterBankItem.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
            }

            binding.root.setOnClickListener {
                bank.isSelected = !bank.isSelected
                if (bank.isSelected) {
                    binding.rlFilterBankItem.background = context.resources.getDrawable(R.drawable.rounded_sides_blue_background, null)
                    binding.tvBankName.setTextColor(context.resources.getColor(R.color.white, null))
                    binding.tvBankAccountNumber.setTextColor(context.resources.getColor(R.color.white, null))
                    binding.rlFilterBankItem.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
                } else {
                    binding.rlFilterBankItem.background = context.resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
                    binding.tvBankName.setTextColor(context.resources.getColor(R.color.text_blue, null))
                    binding.tvBankAccountNumber.setTextColor(context.resources.getColor(R.color.text_grey, null))
                    binding.rlFilterBankItem.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<BankFilterModel>() {
        override fun areItemsTheSame(oldItem: BankFilterModel, newItem: BankFilterModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BankFilterModel, newItem: BankFilterModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var banksList: List<BankFilterModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankItemViewHolder {
        return BankItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_filter_bank_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return banksList.size
    }

    override fun onBindViewHolder(holder: BankItemViewHolder, position: Int) {
        val bank = banksList[position]
        holder.bind(bank, context)
    }

}