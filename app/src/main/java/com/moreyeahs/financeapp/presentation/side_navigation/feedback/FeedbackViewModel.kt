package com.moreyeahs.financeapp.presentation.side_navigation.feedback

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.FeedbackRequest
import com.moreyeahs.financeapp.data.remote.dto.response.FeedbackResponse
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.logoutUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val financeRepo: FinanceRepo, private val preferencesManager: PreferencesManager
) : ViewModel() {

    var userId = ""

    fun getPreferenceData() {
        viewModelScope.launch {
            userId = preferencesManager.getString(Constants.USER_ID) ?: ""
        }
    }

    fun postFeedback(
        feedbackRequest: FeedbackRequest,
        context: Context,
        activity: Activity?,
        onResponse: (FeedbackResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val postFeedback = financeRepo.postFeedback(feedbackRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("SignUpViewModel", "authUserRegister: Error :: ${postFeedback.message}")
                    onError.invoke(postFeedback.message ?: "")
                    if (postFeedback.message.equals("Your token is invalid or expired", true)) {
                        logoutUser(preferencesManager, financeRepo, context, activity)
                    }
                }

                is NetworkingResult.Success -> {
                    Log.d("SignUpViewModel", "authUserRegister: Success :: ${postFeedback.data}")
                    onResponse.invoke(postFeedback.data)
                }
            }
        }
    }

}