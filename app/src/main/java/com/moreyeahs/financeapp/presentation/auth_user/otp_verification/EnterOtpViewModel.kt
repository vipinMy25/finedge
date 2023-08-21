package com.moreyeahs.financeapp.presentation.auth_user.otp_verification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.VerifyOtpRequest
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterOtpViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var emailId = ""
    var fromFrag = ""

    fun verifyOtp(verifyOtpRequest: VerifyOtpRequest, onResponse: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val verifyOtpResponse = authUserRepo.verifyOtp(verifyOtpRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("EnterOtpViewModel", "authUserRegister: Error :: ${verifyOtpResponse.message}")
                    onError.invoke(verifyOtpResponse.message ?: "")
                }

                is NetworkingResult.Success -> {
                    Log.d("EnterOtpViewModel", "authUserRegister: Success :: ${verifyOtpResponse.data}")
                    preferencesManager.putBoolean(Constants.IS_USER_LOGGED_IN, true)
                    preferencesManager.putString(Constants.USER_ID, verifyOtpResponse.data?.data?._id ?: "")
                    preferencesManager.putString(Constants.USER_NAME, verifyOtpResponse.data?.data?.fullName ?: "")
                    preferencesManager.putString(Constants.USER_MOBILE_NUMBER, verifyOtpResponse.data?.data?.Mobile_No ?: "")
                    preferencesManager.putString(Constants.USER_EMAIL, verifyOtpResponse.data?.data?.Email ?: "")
                    onResponse.invoke()
                }
            }
        }
    }

}