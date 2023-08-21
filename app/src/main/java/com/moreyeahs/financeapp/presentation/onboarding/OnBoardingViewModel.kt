package com.moreyeahs.financeapp.presentation.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moreyeahs.financeapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class OnBoardingViewModel : ViewModel() {

    val onBoardingItemObserver = MutableLiveData<ArrayList<OnBoardingItemModel>>()

    init {
        setOnBoardingItems()
    }

    private fun setOnBoardingItems() {
        val itemList = arrayListOf<OnBoardingItemModel>()

        itemList.add(
            OnBoardingItemModel(
                R.drawable.cvp_one,
                "Budget Management",
                "Managing you finances doesn’t have to be challenging. MyFin makes it effortless."
            )
        )
        itemList.add(
            OnBoardingItemModel(
                R.drawable.cvp_two,
                "Set Goal",
                "Setting goals gives you long-term vision and motivation. it allows you to evaluate your expense."
            )
        )
        itemList.add(
            OnBoardingItemModel(
                R.drawable.cvp_three,
                "Statistics",
                "It is useful to analysis data easily, we can compare things visually.\n"
            )
        )

        onBoardingItemObserver.postValue(itemList)
    }
}