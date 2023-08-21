package com.moreyeahs.financeapp.presentation.onboarding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

data class OnBoardingItemModel(
     var onBoardingImage: Int?,
     var onBoardingTitle: String?,
     var onBoardingDesc: String?
)
@BindingAdapter("android:onBoarding_image")
fun loadImage(imageView: AppCompatImageView, imageUrl: Int?) {
    Glide.with(imageView)
        .load(imageUrl)
        .fitCenter()
        .into(imageView)
}