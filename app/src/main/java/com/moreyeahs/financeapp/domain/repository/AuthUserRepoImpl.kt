package com.moreyeahs.financeapp.domain.repository

import android.content.Context
import com.moreyeahs.financeapp.data.remote.api.AuthUserApi
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
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.NetworkingResult
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.isNetworkConnected
import org.json.JSONObject
import javax.inject.Inject

class AuthUserRepoImpl @Inject constructor(
    private val context: Context,
    private val authUserApi: AuthUserApi,
    private val preferencesManager: PreferencesManager
) : AuthUserRepo {

    override suspend fun authRegister(registerRequest: AuthRegisterRequest): NetworkingResult<AuthRegisterResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val response = authUserApi.authRegister(registerRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): NetworkingResult<VerifyOtpResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val response = authUserApi.verifyOtp(verifyOtpRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun authLogin(loginRequest: AuthLoginRequest): NetworkingResult<AuthLoginResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val response = authUserApi.authLogin(loginRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest): NetworkingResult<ForgetPasswordResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val response = authUserApi.forgetPassword(forgetPasswordRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): NetworkingResult<ResetPasswordResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val response = authUserApi.resetPassword(resetPasswordRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun guestLogin(guestLoginRequest: GuestLoginRequest): NetworkingResult<GuestLoginResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val response = authUserApi.guestLogin(guestLoginRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun editProfileGuest(editProfileGuestRequest: EditProfileGuestRequest): NetworkingResult<EditProfileGuestResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = authUserApi.editProfileGuest(accessToken = "Bearer $token", editProfileGuestRequest = editProfileGuestRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

    override suspend fun editProfileUser(editProfileUserRequest: EditProfileUserRequest): NetworkingResult<EditProfileUserResponse> {
        return try {
            if (!isNetworkConnected(context)) {
                NetworkingResult.Error("No Internet Connection!")
            } else {
                val token = preferencesManager.getString(Constants.ACCESS_TOKEN)
                val response = authUserApi.editProfileUser(accessToken = "Bearer $token", editProfileUserRequest = editProfileUserRequest)
                if (response.isSuccessful) {
                    NetworkingResult.Success(response.body()!!)
                } else {
                    var message = "Something went wrong!"
                    val errorBody = response.errorBody()?.string()
                    errorBody.let {
                        val jsonObject = JSONObject(it ?: "")
                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message")
                        }
                    }
                    NetworkingResult.Error(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkingResult.Error(e.message ?: "Something went wrong!")
        }
    }

}