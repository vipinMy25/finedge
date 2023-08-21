package com.moreyeahs.financeapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.getSystem
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings.Secure
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.moreyeahs.financeapp.data.remote.dto.request.PostTransactionRequest
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    return false
}

val Int.toPx: Int get() = (this * getSystem().displayMetrics.density).toInt()

@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String {
    return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
}

fun getLatestDeviceSms(context: Context, fromTime: Long): ArrayList<PostTransactionRequest.Transaction> {
    val lstSms: ArrayList<PostTransactionRequest.Transaction> = ArrayList()

    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
        val cr: ContentResolver = context.contentResolver

        val cursor = cr.query(
            Telephony.Sms.Inbox.CONTENT_URI, arrayOf(
                Telephony.Sms.Inbox.BODY,
                Telephony.Sms.Inbox.DATE,
                Telephony.Sms.Inbox.ADDRESS
            ),
            Telephony.Sms.Inbox.DATE + ">?",
            arrayOf("$fromTime"),
            Telephony.Sms.Inbox.DATE
        )

        val totalSMS = cursor!!.count
        if (cursor.moveToFirst()) {
            for (i in 0 until totalSMS) {
                val body = cursor.getString(0)
                val date = cursor.getString(1)
                val address = cursor.getString(2)

                if (isTransactionSms(body)) {
                    lstSms.add(
                        PostTransactionRequest.Transaction(
                            text = body,
                            timestamp = date,
                            sender = address
                        )
                    )
                }
                cursor.moveToNext()
            }
        } else {
            Log.d("UtilityFunctions", "getLatestDeviceSms: You have no SMS in Inbox")
        }

        cursor.close()
    } else {
        Toast.makeText(context, "Please allow SMS permission", Toast.LENGTH_SHORT).show()
    }

    return lstSms
}

fun isTransactionSms(body: String): Boolean {
    return when {
        body.contains("credited", ignoreCase = true) -> true
        body.contains("debited", ignoreCase = true) -> true
        body.contains("sent", ignoreCase = true) -> true
        body.contains("spent", ignoreCase = true) -> true
        body.contains("paying", ignoreCase = true) -> true
        body.contains("received", ignoreCase = true) && !body.contains("missed call", true) -> true
        else -> {
            false
        }
    }
}

fun logoutUser(prefs: PreferencesManager, financeRepo: FinanceRepo, context: Context, activity: Activity?, isSessionOut: Boolean = true) {
    CoroutineScope(Dispatchers.IO).launch {
        prefs.clearPreferences()
        prefs.putBoolean(Constants.IS_USER_FIRST_TIME, false)
        financeRepo.deleteAll()

        val intent = Intent(context, AuthUserActivity::class.java)
        context.startActivity(intent)
        activity?.finish()

        if (isSessionOut) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Session timed out, please login again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}