package com.moreyeahs.financeapp.presentation.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.util.Constants

class AccountSpinnerAdapter(val context: Context, var accountsList: List<AccountModel>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return accountsList.size
    }

    override fun getItem(position: Int): Any {
        return accountsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_account_spinner_item, parent, false)
            viewHolder = ItemHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ItemHolder
        }

        val itemModel = accountsList[position]

        Glide.with(context)
            .load("${Constants.BASE_URL}/${itemModel.bankLogoUrl}")
            .fitCenter()
            .placeholder(R.drawable.transaction_item_icon)
            .into(viewHolder.ivLogo)

        viewHolder.tvBankName.text = itemModel.userAccountName.ifEmpty { itemModel.bankName }
        viewHolder.tvAccNo.text = itemModel.accountNumber

        return view
    }

    private class ItemHolder(row: View?) {
        val ivLogo: AppCompatImageView
        val tvBankName: AppCompatTextView
        val tvAccNo: AppCompatTextView

        init {
            ivLogo = row?.findViewById(R.id.iv_bank_logo) as AppCompatImageView
            tvBankName = row?.findViewById(R.id.tv_bank_name) as AppCompatTextView
            tvAccNo = row?.findViewById(R.id.tv_account_no) as AppCompatTextView
        }
    }

}