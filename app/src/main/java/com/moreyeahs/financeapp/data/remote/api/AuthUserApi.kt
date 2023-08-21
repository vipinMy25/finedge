package com.moreyeahs.financeapp.data.remote.api

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthUserApi {

    @POST("/Login")
    suspend fun authLogin(
        @Body authLoginRequest: AuthLoginRequest
    ): Response<AuthLoginResponse>

    @POST("/Signup")
    suspend fun authRegister(
        @Body authRegisterRequest: AuthRegisterRequest
    ): Response<AuthRegisterResponse>

    @POST("/verifyOtp")
    suspend fun verifyOtp(
        @Body verifyOtpRequest: VerifyOtpRequest
    ): Response<VerifyOtpResponse>

    @POST("/ForgetPassword")
    suspend fun forgetPassword(
        @Body forgetPasswordRequest: ForgetPasswordRequest
    ): Response<ForgetPasswordResponse>

    @POST("/ResetPassword")
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @POST("/guestLogin")
    suspend fun guestLogin(
        @Body guestLoginRequest: GuestLoginRequest
    ): Response<GuestLoginResponse>

    @PUT("/editProfileGuest")
    suspend fun editProfileGuest(
        @Header("Authorization") accessToken: String,
        @Body editProfileGuestRequest: EditProfileGuestRequest
    ): Response<EditProfileGuestResponse>

    @PUT("/editProfileUser")
    suspend fun editProfileUser(
        @Header("Authorization") accessToken: String,
        @Body editProfileUserRequest: EditProfileUserRequest
    ): Response<EditProfileUserResponse>

}