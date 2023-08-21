package com.moreyeahs.financeapp.presentation.auth_user.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.AuthLoginRequest
import com.moreyeahs.financeapp.data.remote.dto.response.AuthLoginResponse
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.Constants.USER_EMAIL
import com.moreyeahs.financeapp.util.Constants.USER_MOBILE_NUMBER
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun authUserLogin(authUserRequest: AuthLoginRequest, isRememberMeChecked: Boolean, onResponse: (AuthLoginResponse?) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val loginResponse = authUserRepo.authLogin(authUserRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("SignUpViewModel", "authUserRegister: Error :: ${loginResponse.message}")
                    onError.invoke(loginResponse.message ?: "")
                }

                is NetworkingResult.Success -> {
                    Log.d("SignUpViewModel", "authUserRegister: Success :: ${loginResponse.data}")
                    if (isRememberMeChecked) {
                        preferencesManager.putBoolean(Constants.IS_USER_LOGGED_IN, true)
                    }
                    preferencesManager.putString(Constants.USER_ID, loginResponse.data?.data?._id ?: "")
                    preferencesManager.putString(Constants.USER_NAME, loginResponse.data?.data?.fullName ?: "")
                    preferencesManager.putString(USER_MOBILE_NUMBER, loginResponse.data?.data?.Mobile_No ?: "")
                    preferencesManager.putString(USER_EMAIL, loginResponse.data?.data?.Email ?: "")
                    preferencesManager.putString(Constants.ACCESS_TOKEN, loginResponse.data?.token ?: "")
                    val lastSyncSmsTime = loginResponse.data?.time?.lastSyncSmsTime
                    lastSyncSmsTime?.let {
                        preferencesManager.putString(Constants.LAST_SYNC_MESSAGE_TIME, it.toString())
                    }
                    onResponse.invoke(loginResponse.data)
                }
            }
        }
    }

}