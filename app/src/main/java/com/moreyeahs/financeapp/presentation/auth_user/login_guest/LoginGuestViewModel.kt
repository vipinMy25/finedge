package com.moreyeahs.financeapp.presentation.auth_user.login_guest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.GuestLoginRequest
import com.moreyeahs.financeapp.data.remote.dto.response.GuestLoginResponse
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginGuestViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun guestLogin(guestLoginRequest: GuestLoginRequest, onResponse: (GuestLoginResponse?) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val response = authUserRepo.guestLogin(guestLoginRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("LoginGuestViewModel", "guestLogin: Error :: ${response.message}")
                    onError.invoke(response.message ?: "")
                }

                is NetworkingResult.Success -> {
                    Log.d("SignUpViewModel", "guestLogin: Success :: ${response.data}")
                    preferencesManager.putBoolean(Constants.IS_USER_LOGGED_IN, true)
                    preferencesManager.putString(Constants.USER_ID, response.data?.data?._id ?: "")
                    preferencesManager.putString(Constants.USER_NAME, response.data?.data?.fullName ?: "")
                    preferencesManager.putString(Constants.USER_MOBILE_NUMBER, response.data?.data?.Mobile_No ?: "")
                    preferencesManager.putString(Constants.USER_EMAIL, response.data?.data?.Email ?: "")
                    preferencesManager.putString(Constants.ACCESS_TOKEN, response.data?.token ?: "")
                    preferencesManager.putBoolean(Constants.IS_GUEST_USER, true)
                    val lastSyncSmsTime = response.data?.Maxtime?.lastSyncSmsTime
                    lastSyncSmsTime?.let {
                        preferencesManager.putString(Constants.LAST_SYNC_MESSAGE_TIME, it.toString())
                    }
                    onResponse.invoke(response.data)
                }
            }
        }
    }

}