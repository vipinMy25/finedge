package com.moreyeahs.financeapp.presentation.auth_user.reset_password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.ResetPasswordRequest
import com.moreyeahs.financeapp.data.remote.dto.response.ResetPasswordResponse
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.util.NetworkingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo
) : ViewModel() {

    var emailId = ""

    fun authResetPassword(resetPasswordRequest: ResetPasswordRequest, onResponse: (ResetPasswordResponse?) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val response = authUserRepo.resetPassword(resetPasswordRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("ResetPasswordViewModel", "authResetPassword: Error :: ${response.message}")
                    onError.invoke(response.message ?: "")
                }

                is NetworkingResult.Success -> {
                    Log.d("ResetPasswordViewModel", "authResetPassword: Success :: ${response.data}")
                    onResponse.invoke(response.data)
                }
            }
        }
    }

}