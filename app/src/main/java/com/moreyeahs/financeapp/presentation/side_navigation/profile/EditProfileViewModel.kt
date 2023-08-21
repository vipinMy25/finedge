package com.moreyeahs.financeapp.presentation.side_navigation.profile

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moreyeahs.financeapp.data.remote.dto.request.EditProfileGuestRequest
import com.moreyeahs.financeapp.data.remote.dto.request.EditProfileUserRequest
import com.moreyeahs.financeapp.data.remote.dto.response.EditProfileGuestResponse
import com.moreyeahs.financeapp.data.remote.dto.response.EditProfileUserResponse
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.util.Constants.IS_GUEST_USER
import com.moreyeahs.financeapp.util.Constants.USER_EMAIL
import com.moreyeahs.financeapp.util.Constants.USER_MOBILE_NUMBER
import com.moreyeahs.financeapp.util.Constants.USER_NAME
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.logoutUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authUserRepo: AuthUserRepo,
    private val financeRepo: FinanceRepo,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var isGuestUser = false
    var userName = ""
    var userMobile = ""
    var userEmail = ""

    fun getPreferenceData() {
        viewModelScope.launch {
            userName = preferencesManager.getString(USER_NAME) ?: ""
            userMobile = preferencesManager.getString(USER_MOBILE_NUMBER) ?: ""
            userEmail = preferencesManager.getString(USER_EMAIL) ?: ""
            isGuestUser = preferencesManager.getBoolean(IS_GUEST_USER) ?: false
        }
    }

    fun updateNameInPrefs(name: String) {
        viewModelScope.launch {
            preferencesManager.putString(USER_NAME, name)
        }
    }

    fun updateMobileInPrefs(mobile: String) {
        viewModelScope.launch {
            preferencesManager.putString(USER_MOBILE_NUMBER, mobile)
        }
    }

    fun updateGuestProfile(editProfileGuestRequest: EditProfileGuestRequest, context: Context, activity: Activity?, onResponse: (EditProfileGuestResponse?) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val response = authUserRepo.editProfileGuest(editProfileGuestRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("EditProfileViewModel", "updateGuestProfile: Error :: ${response.message}")
                    onError.invoke(response.message ?: "")
                    if (response.message.equals("Your token is invalid or expired", true)) {
                        logoutUser(preferencesManager, financeRepo, context, activity)
                    }
                }

                is NetworkingResult.Success -> {
                    Log.d("EditProfileViewModel", "updateGuestProfile: Success :: ${response.data}")
                    onResponse.invoke(response.data)
                }
            }
        }
    }

    fun updateUserProfile(editProfileUserRequest: EditProfileUserRequest, context: Context, activity: Activity?, onResponse: (EditProfileUserResponse?) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val response = authUserRepo.editProfileUser(editProfileUserRequest)) {
                is NetworkingResult.Error -> {
                    Log.d("EditProfileViewModel", "updateUserProfile: Error :: ${response.message}")
                    onError.invoke(response.message ?: "")
                    if (response.message.equals("Your token is invalid or expired", true)) {
                        logoutUser(preferencesManager, financeRepo, context, activity)
                    }
                }

                is NetworkingResult.Success -> {
                    Log.d("EditProfileViewModel", "updateUserProfile: Success :: ${response.data}")
                    onResponse.invoke(response.data)
                }
            }
        }
    }

}