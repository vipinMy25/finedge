package com.moreyeahs.financeapp.presentation.dashboard.account

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.AllAccountItemLayoutBinding
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.PreferencesManager

class AllAccountAdapter(
    val context: Context,
    val activity: Activity?,
    var categorisedList: HashMap<String, List<AccountModel>>,
    val financeRepo: FinanceRepo,
    val preferencesManager: PreferencesManager,
    var onNotify: () -> Unit
) : RecyclerView.Adapter<AllAccountAdapter.AllAccountViewHolder>() {

    class AllAccountViewHolder(private val binding: AllAccountItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            key: String,
            value: List<AccountModel>?,
            context: Context,
            activity: Activity?,
            financeRepo: FinanceRepo,
            preferencesManager: PreferencesManager,
            onNotify: () -> Unit
        ) {
            binding.tvHeaderTitle.text = key.uppercase()

            value?.let {
                val accountItemAdapter = AccountItemAdapter(context, activity, value.toSet().toList(), financeRepo, preferencesManager) {
                    onNotify.invoke()
                }
                binding.rvBank.adapter = accountItemAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllAccountViewHolder {
        return AllAccountViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.all_account_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return categorisedList.size
    }

    override fun onBindViewHolder(holder: AllAccountViewHolder, position: Int) {
        val keys = categorisedList.keys.toList()
        val key = keys[position]
        val value = categorisedList[key]
        holder.bind(key, value, context, activity, financeRepo, preferencesManager) {
            onNotify.invoke()
        }
    }

    fun notifyList(categorisedList: java.util.HashMap<String, List<AccountModel>>) {
        this.categorisedList = categorisedList
        notifyDataSetChanged()
    }

}