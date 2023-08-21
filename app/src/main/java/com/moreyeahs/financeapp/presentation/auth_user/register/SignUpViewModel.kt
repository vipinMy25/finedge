package com.moreyeahs.financeapp.presentation.auth_user.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.AuthRegisterRequest
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun authUserRegister(authUserRequest: AuthRegisterRequest, onResponse: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val authUserRegisterResponse = authUserRepo.authRegister(authUserRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("SignUpViewModel", "authUserRegister: Error :: ${authUserRegisterResponse.message}")
                    onError.invoke(authUserRegisterResponse.message ?: "")
                }

                is NetworkingResult.Success -> {
                    Log.d("SignUpViewModel", "authUserRegister: Success :: ${authUserRegisterResponse.data}")
                    preferencesManager.putString(Constants.ACCESS_TOKEN, authUserRegisterResponse.data?.token ?: "")
                    onResponse.invoke(authUserRegisterResponse.data?.token ?: "")
                }
            }
        }
    }

}