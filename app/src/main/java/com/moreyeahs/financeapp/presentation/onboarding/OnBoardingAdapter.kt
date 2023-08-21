package com.moreyeahs.financeapp.presentation.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moreyeahs.financeapp.databinding.OnBoardingItemLayoutBinding

class OnBoardingAdapter(
    private val context: Context, private val onBoardingItemList: ArrayList<OnBoardingItemModel>
) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OnBoardingAdapter.OnBoardingViewHolder {
        val binding =
            OnBoardingItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingAdapter.OnBoardingViewHolder, position: Int) {
        val item = onBoardingItemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = onBoardingItemList.size

    inner class OnBoardingViewHolder(private val binding: OnBoardingItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(onBoardingItemModel: OnBoardingItemModel) {
            binding.onBoardingItem = onBoardingItemModel
            binding.executePendingBindings()
        }

    }

}

