package com.moreyeahs.financeapp.util

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Utils {

    fun isValidPhoneNumber(str: String?): Boolean {

        val regex = ("(0/91)?[6-9][0-9]{9}")

        // Compile the ReGex
        val p = Pattern.compile(regex)

        // If the string is empty
        // return false
        if (str == null) {
            return false
        }

        // Pattern class contains matcher()
        // method to find the matching
        // between the given string
        // and the regular expression.
        val m = p.matcher(str)

        // Return if the string
        // matched the ReGex
        return m.matches()
    }

    fun showCustomDialogBottum(context: Context?, resId: Int): Dialog {
        val dialog = Dialog(context!!)
        try {
            Objects.requireNonNull(dialog.window)
                ?.setBackgroundDrawableResource(android.R.color.transparent)
        } catch (_: Exception) {

        }

        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(resId)

        dialog.setCancelable(false)


        val windo = dialog.window
        windo!!.setDimAmount(0.5f)
        val wlp = windo.attributes
        wlp.gravity = Gravity.BOTTOM
        try {
            windo.setBackgroundDrawableResource(android.R.color.transparent)
        } catch (_: Exception) {
        }
        windo.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        windo.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        windo.attributes = wlp
        return dialog
    }

    fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility // get current flag
            flags =
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
            activity.window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = Color.WHITE // optional
        }
    }

    /* fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }*/
    fun getDateTime(currentTime: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val date = calendar.time
        val sdf = SimpleDateFormat("MMM dd yyyy hh:mm a")
        return sdf.format(date)
    }

    fun getDateFromLong(currentTime: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val date = calendar.time
        val sdf = SimpleDateFormat("MMM dd")
        return sdf.format(date)
    }

    fun getMonthYearFromLong(currentTime: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val date = calendar.time
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun getTimeFromMonthYearString(time: String): Long {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val date = sdf.parse(time)
        date?.let {
            return it.time
        } ?: return 0
    }

    fun getMoth(currentTime: Long): String? {
        val finalDate: String
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val date = calendar.time
        finalDate = date.toString()
        val sdf = SimpleDateFormat("M")
        return sdf.format(date)
    }

    fun getFormattedDecimal(value: Double): String {
        val formattedString = DecimalFormat("##,##,##,###.##").format(value).toString()
        val splittedString = formattedString.split(".")

        if (splittedString.size > 1 && splittedString[1].take(1) == "0") {
            return splittedString[0]
        }

        return formattedString
    }

    fun getCurrentPhoneNumber(context: Context): String {
        return try {
            val telemamanger = context.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
            //val getSimSerialNumber = telemamanger.simSerialNumber
            val getSimNumber = if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return ""
            } else {
                telemamanger.line1Number
            }
            Log.d("Sim Number", "$getSimNumber")
            return getSimNumber
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }


    }


}