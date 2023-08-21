package com.moreyeahs.financeapp.presentation.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.LayoutTransactionListItemBinding
import com.moreyeahs.financeapp.domain.model.TransactionModel
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.SwipeLayout
import com.moreyeahs.financeapp.util.Utils
import java.lang.Exception

class HomeTransactionAdapter(var context: Context) : RecyclerView.Adapter<HomeTransactionAdapter.TransactionItemViewHolder>() {

    private var prevSwipedLayout: SwipeLayout? = null
    var editClick: ((TransactionModel) -> Unit)? = null
    var deleteClick: ((TransactionModel) -> Unit)? = null

    class TransactionItemViewHolder(val binding: LayoutTransactionListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionModel: TransactionModel, context: Context) {
            if (transactionModel.upiID.isNotEmpty()) {
                binding.tvTitle.text = transactionModel.upiID
            } else {
                if (transactionModel.sms.contains("upi", ignoreCase = true)) {
                    binding.tvTitle.text = "UPI"
                } else {
                    binding.tvTitle.text = transactionModel.bankName
                }
            }

            try {
                if (transactionModel.amount.toDoubleOrNull()!! > 0) {
                    if (transactionModel.transactionType.equals("Credit", ignoreCase = true)) {
                        binding.tvTransactionItemAmount.text = "+  ₹${transactionModel.amount.toDoubleOrNull()}"
                        binding.tvTransactionItemAmount.setTextColor(context.getColor(R.color.green))
                    } else {
                        binding.tvTransactionItemAmount.text = "-  ₹${transactionModel.amount.toDoubleOrNull()}"
                        binding.tvTransactionItemAmount.setTextColor(context.getColor(R.color.red))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (transactionModel.accountNumber.isNotEmpty() && transactionModel.accountNumber.length > 2) {
                binding.tvAccountNum.text = transactionModel.accountNumber
            }

            binding.tvDate.text = Utils.getDateTime(transactionModel.timestamp.toLong())

            Glide.with(context)
                .load("${Constants.BASE_URL}/${transactionModel.bankLogoUrl}")
                .fitCenter()
                .placeholder(R.drawable.transaction_item_icon)
                .into(binding.ivTransactionItemIcon)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<TransactionModel>() {
        override fun areItemsTheSame(oldItem: TransactionModel, newItem: TransactionModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: TransactionModel, newItem: TransactionModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var transactionModelItems: List<TransactionModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItemViewHolder {
        return TransactionItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_transaction_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return transactionModelItems.size
    }

    override fun onBindViewHolder(holder: TransactionItemViewHolder, position: Int) {
        val smsItem = transactionModelItems[position]
        holder.bind(smsItem, context)

        if (itemCount - 1 == position) {
            holder.binding.viewTransactionItemDivider.visibility = View.GONE
        }

        setSwipeListener(holder)

        holder.binding.ivEdit.setOnClickListener {
            editClick?.invoke(smsItem)
        }

        holder.binding.ivDelete.setOnClickListener {
            deleteClick?.invoke(smsItem)
        }
    }

    private fun setSwipeListener(viewHolder: TransactionItemViewHolder) {
        viewHolder.binding.swipeLayout.apply {
            showMode = SwipeLayout.ShowMode.LayDown
            addDrag(SwipeLayout.DragEdge.Right, viewHolder.binding.wrapperRight)
            val swipeListener: SwipeLayout.SwipeListener = object : SwipeLayout.SwipeListener {
                override fun onClose(layout: SwipeLayout?) {
                    //
                }

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                    //
                }

                override fun onStartOpen(layout: SwipeLayout?) {
                    //
                }

                override fun onOpen(layout: SwipeLayout?) {
                    if (prevSwipedLayout != null && layout !== prevSwipedLayout) {
                        prevSwipedLayout?.close()
                    }
                    prevSwipedLayout = layout
                }

                override fun onStartClose(layout: SwipeLayout?) {
                    //
                }

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                    //
                }
            }
            addSwipeListener(swipeListener)
        }
    }

}