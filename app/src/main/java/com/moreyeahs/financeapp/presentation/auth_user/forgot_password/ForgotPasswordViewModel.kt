package com.moreyeahs.financeapp.presentation.auth_user.forgot_password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.ForgetPasswordRequest
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.util.NetworkingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo
) : ViewModel() {

    fun authForgetPassword(forgetPasswordRequest: ForgetPasswordRequest, onResponse: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val response = authUserRepo.forgetPassword(forgetPasswordRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("ForgotPasswordViewModel", "authForgetPassword: Error :: ${response.message}")
                    onError.invoke(response.message ?: "")
                }

                is NetworkingResult.Success -> {
                    Log.d("ForgotPasswordViewModel", "authForgetPassword: Success :: ${response.data}")
                    onResponse.invoke(response.data?.message ?: "")
                }
            }
        }
    }

}