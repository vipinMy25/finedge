package com.moreyeahs.financeapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log


class SmsListener: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null && intent.action!!.equals("android.provider.Telephony.SMS_RECEIVED", ignoreCase = true)) {
            val bundle = intent.extras
            if (bundle != null) {
                val sms = bundle.get("pdus") as Array<Any>?
                val smsMsg = StringBuilder()

                var smsMessage: SmsMessage
                if (sms != null) {
                    for (sm in sms) {
                        val format = bundle.getString("format")
                        smsMessage = SmsMessage.createFromPdu(sm as ByteArray, format)

                        val msgBody = smsMessage.messageBody.toString()
                        val msgAddress = smsMessage.originatingAddress

                        smsMsg.append("SMS from : ").append(msgAddress).append("\n")
                        smsMsg.append(msgBody).append("\n")
                    }

                    Log.d("SmsListener", "onReceive: $smsMsg")
                }
            }
        }
    }
}