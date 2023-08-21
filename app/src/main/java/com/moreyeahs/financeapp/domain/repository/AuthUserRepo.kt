package com.moreyeahs.financeapp.domain.repository

import com.moreyeahs.financeapp.data.remote.dto.request.AuthLoginRequest
import com.moreyeahs.financeapp.data.remote.dto.request.AuthRegisterRequest
import com.moreyeahs.financeapp.data.remote.dto.request.EditProfileGuestRequest
import com.moreyeahs.financeapp.data.remote.dto.request.EditProfileUserRequest
import com.moreyeahs.financeapp.data.remote.dto.request.ForgetPasswordRequest
import com.moreyeahs.financeapp.data.remote.dto.request.GuestLoginRequest
import com.moreyeahs.financeapp.data.remote.dto.request.ResetPasswordRequest
import com.moreyeahs.financeapp.data.remote.dto.request.VerifyOtpRequest
import com.moreyeahs.financeapp.data.remote.dto.response.AuthLoginResponse
import com.moreyeahs.financeapp.data.remote.dto.response.AuthRegisterResponse
import com.moreyeahs.financeapp.data.remote.dto.response.EditProfileGuestResponse
import com.moreyeahs.financeapp.data.remote.dto.response.EditProfileUserResponse
import com.moreyeahs.financeapp.data.remote.dto.response.ForgetPasswordResponse
import com.moreyeahs.financeapp.data.remote.dto.response.GuestLoginResponse
import com.moreyeahs.financeapp.data.remote.dto.response.ResetPasswordResponse
import com.moreyeahs.financeapp.data.remote.dto.response.VerifyOtpResponse
import com.moreyeahs.financeapp.util.NetworkingResult

interface AuthUserRepo {

    suspend fun authRegister(registerRequest: AuthRegisterRequest): NetworkingResult<AuthRegisterResponse>
    suspend fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): NetworkingResult<VerifyOtpResponse>
    suspend fun authLogin(loginRequest: AuthLoginRequest): NetworkingResult<AuthLoginResponse>
    suspend fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest): NetworkingResult<ForgetPasswordResponse>
    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): NetworkingResult<ResetPasswordResponse>
    suspend fun guestLogin(guestLoginRequest: GuestLoginRequest): NetworkingResult<GuestLoginResponse>
    suspend fun editProfileGuest(editProfileGuestRequest: EditProfileGuestRequest): NetworkingResult<EditProfileGuestResponse>
    suspend fun editProfileUser(editProfileUserRequest: EditProfileUserRequest): NetworkingResult<EditProfileUserResponse>

}