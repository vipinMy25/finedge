package com.moreyeahs.financeapp.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.moreyeahs.financeapp.FinanceApplication
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.domain.model.TransactionModel
import java.util.Calendar
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern

class SmsUtils {
    companion object {


        fun getLatestDeviceSms(): ArrayList<TransactionModel> {
            val lstSms: ArrayList<TransactionModel> = ArrayList()

            if (ContextCompat.checkSelfPermission(
                    FinanceApplication.getApplicationContext(),
                    "android.permission.READ_SMS"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cursor = FinanceApplication.getApplicationContext().contentResolver.query(
                    Uri.parse("content://sms/inbox"),
                    null,
                    "date" + ">?",
                    arrayOf("" + Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000).time),
                    "date asc"
                )
                val calender: Calendar = Calendar.getInstance()
                var currentMoth = calender.get(Calendar.MONTH) + 1
                var accountType = ""
                while (cursor!!.moveToNext()) {
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                    val senderId = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"))
                    var month = Utils.getMoth(date.toLong())
                    //     Log.d("Month **", "" + month)

                    // val currentMoth = Utils.getMoth()
                    //     Log.d("Current Month **", "" + calender.get(Calendar.MONTH))
                    if (checkMesaage(body)) {
                        if (month.equals(currentMoth.toString())) {
                            var transactionType = checkMesaageType(body)
                            var upiIDPattern = Pattern.compile("\\b([a-zA-Z0-9]+@[a-zA-Z0-9]+)\\b")
                            var transIdPattern = Pattern.compile("\\b(TXN\\d+)\\b")
                            var paytmQRPattern = Pattern.compile("\\b(paytm[a-zA-Z0-9]+)\\b")


                            var body2 = body.toString()
                            var b = body2.replace("[-+^:,@_/]".toRegex(), "")
                            val finals: String = b.replace("\\s+".toRegex(), "")
                            //  Log.d("SMS Body *** ", "" + finals)


                            val amountINR = Pattern.compile("INR([0-9]+)")
                            val amountINRDot = Pattern.compile("INR.([0-9]+)")
                            val amountRs = Pattern.compile("Rs([0-9]+)")
                            val amountRsDot = Pattern.compile("Rs.([0-9]+)")


                            val amountMatcherINR = amountINR.matcher(finals)
                            val amountMatcherINRDot = amountINRDot.matcher(finals)
                            val amountMatcherRs = amountRs.matcher(finals)
                            val amountMatcherRsDot = amountRsDot.matcher(finals)


                            var reqAC = Pattern.compile("ac\\s*([\\d,]+)")
                            var reqACDot = Pattern.compile("ac.\\s*([\\d,]+)")
                            var reqACxx = Pattern.compile("acxx\\s*([\\d,]+)")
                            var reqACDotxx = Pattern.compile("ac.xx\\s*([\\d,]+)")
                            var reqxxxx = Pattern.compile("xxxx\\s*([\\d,]+)")
                            var reqacnoxx = Pattern.compile("acnoxx\\s*([\\d,]+)")
                            var reqacendingwith = Pattern.compile("acendingwith\\s*([\\d,]+)")


                            val reqACMatcher = reqAC.matcher(finals.lowercase())
                            val reqACDotMatcher = reqACDot.matcher(finals.lowercase())
                            val reqACDotxMatcher = reqACDotxx.matcher(finals.lowercase())
                            val reqACXXMatcher = reqACxx.matcher(finals.lowercase())
                            val reqxxxMatcher = reqxxxx.matcher(finals.lowercase())
                            val reqcnoMatcher = reqacnoxx.matcher(finals.lowercase())
                            val reqacendingMatcher = reqacendingwith.matcher(finals.lowercase())

                            val transIDmatcher: Matcher = transIdPattern.matcher(body)
                            val qrmatcher: Matcher = paytmQRPattern.matcher(body)
                            val upimatcher: Matcher = upiIDPattern.matcher(body)


                            var amount = ""
                            var upiId = ""
                            var accountNo = ""

                            accountType = checkAccountType(body)




                            if (reqACMatcher.find()) {
                                accountNo = reqACMatcher.group(1)
                            }
                            if (reqACDotMatcher.find()) {
                                accountNo = reqACDotMatcher.group(1) as String
                            }
                            if (reqACDotxMatcher.find()) {
                                accountNo = reqACDotxMatcher.group(1) as String
                            }
                            if (reqACXXMatcher.find()) {
                                accountNo = reqACXXMatcher.group(1) as String
                            }
                            if (reqxxxMatcher.find()) {
                                accountNo = reqxxxMatcher.group(1) as String
                            }
                            if (reqcnoMatcher.find()) {
                                accountNo = reqcnoMatcher.group(1) as String
                            }
                            if (reqacendingMatcher.find()) {
                                accountNo = reqacendingMatcher.group(1) as String
                            }






                            if (transIDmatcher.find()) {
                                upiId = transIDmatcher.group(1)
                            }
                            if (upimatcher.find()) {
                                upiId = upimatcher.group(1)
                            }
                            if (qrmatcher.find()) {
                                upiId = qrmatcher.group(1)
                            }
                            if (amountMatcherINR.find()) {
                                amount = amountMatcherINR.group(1);
                            } else if (amountMatcherINRDot.find()) {
                                amount = amountMatcherINRDot.group(1);
                            } else if (amountMatcherRs.find()) {
                                amount = amountMatcherRs.group(1);
                            } else if (amountMatcherRsDot.find()) {
                                amount = amountMatcherRsDot.group(1);
                            }

                            Log.d("Account NUmber **", "" + accountNo)

                            lstSms.add(
                                TransactionModel(
                                    "",
                                    date,
                                    body,
                                    senderId,
                                    transactionType,
                                    amount,
                                    upiId,
                                    accountType,
                                    "",
                                    accountNo,
                                    ""
                                )
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(
                    FinanceApplication.getApplicationContext(),
                    "Please allow sms permission",
                    Toast.LENGTH_SHORT
                ).show()
            }

            return lstSms
        }

        fun getAllDeviceSms(): ArrayList<TransactionModel> {
            val lstSms: ArrayList<TransactionModel> = ArrayList()

            if (ContextCompat.checkSelfPermission(
                    FinanceApplication.getApplicationContext(),
                    "android.permission.READ_SMS"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cursor = FinanceApplication.getApplicationContext().contentResolver.query(
                    Uri.parse("content://sms/inbox"),
                    null,
                    null,
                    null,
                    "date asc"
                )
                val calender: Calendar = Calendar.getInstance()
                var currentMoth = calender.get(Calendar.MONTH) + 1
                var accountType = ""
                while (cursor!!.moveToNext()) {
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                    val senderId = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"))

                    if (checkMesaage(body)
                    ) {

                        var transactionType = checkMesaageType(body)

                        accountType = checkAccountType(body)

                        val sbiPattern = Pattern.compile("\\b(\\d+(?:\\.\\d+)?)\\b")
                        var upiIDPattern = Pattern.compile("\\b([a-zA-Z0-9.]+@[a-zA-Z0-9]+)\\b")
                        var transIdPattern = Pattern.compile("\\b(TXN\\d+)\\b")
                        var paytmQRPattern = Pattern.compile("\\b(paytm[a-zA-Z0-9]+)\\b")
                        var receivedFromPattern = Pattern.compile("received from [A-Za-z]+ [A-Za-z]+")
                        var receivedFromPattern2 = Pattern.compile("received from [A-Za-z]+  [A-Za-z]+")
                        var transferToPattern = Pattern.compile("transfer to [A-Za-z]+ [A-Za-z]+")
                        var transferToPattern2 = Pattern.compile("transfer to [A-Za-z]+  [A-Za-z]+")
                        var creditCardPattern = Pattern.compile("""spent on (.*?) card""")
                        var creditCardPattern2 = Pattern.compile("""received towards (.*?) card""")
                        var creditCardPattern3 = Pattern.compile("""credited to (.*?) card""")
                        var creditCardPattern4 = Pattern.compile("""on (.*?) card""")
                        val atPattern = Pattern.compile("At [A-Za-z]+ [A-Za-z]+")
                        var loanPattern =
                            Pattern.compile(
                                "\\bloan\\b|\\bamount\\b|\\bcredit\\b",
                                Pattern.CASE_INSENSITIVE
                            )

                        var body2 = body.toString()
                        var b = body2.replace("[-+^:,@_/]".toRegex(), "")
                        val finals: String = b.replace("\\s+", "")

                        val amountINR = Pattern.compile("INR([0-9]+)")
                        val amountINRDot = Pattern.compile("INR.([0-9]+)")
                        val amountRs = Pattern.compile("Rs([0-9]+)")
                        val amountRsSpace = Pattern.compile("Rs ([0-9]+)")
                        val amountRsDot = Pattern.compile("Rs.([0-9]+)")
                        val amountRsDotSpace = Pattern.compile("Rs. ([0-9]+)")

                        var reqAC = Pattern.compile("ac\\s*([\\d,]+)")
                        var reqACDot = Pattern.compile("ac.\\s*([\\d,]+)")
                        var reqACxx = Pattern.compile("acxx\\s*([\\d,]+)")
                        var reqACx = Pattern.compile("ac x\\s*([\\d,]+)")
                        var reqACDotxx = Pattern.compile("ac.xx\\s*([\\d,]+)")
                        var reqxxxx = Pattern.compile("xxxx\\s*([\\d,]+)")
                        var reqacnoxx = Pattern.compile("acnoxx\\s*([\\d,]+)")
                        var reqacendingwith = Pattern.compile("acendingwith\\s*([\\d,]+)")
                        var reqacending = Pattern.compile("ending \\s*([\\d,]+)")
                        var reqAcNo = Pattern.compile("ac no. xx\\s*([\\d,]+)")
                        var reqCardNo = Pattern.compile("card xx\\s*([\\d,]+)")
                        var reqCardNo2 = Pattern.compile("card no. xx\\s*([\\d,]+)")
                        var reqUpiNo = Pattern.compile("ac \\s*(\\d\\d\\w\\w\\d\\d\\d\\d)")

                        val reqACMatcher = reqAC.matcher(finals.lowercase())
                        val reqACDotMatcher = reqACDot.matcher(finals.lowercase())
                        val reqACDotxMatcher = reqACDotxx.matcher(finals.lowercase())
                        val reqACXXMatcher = reqACxx.matcher(finals.lowercase())
                        val reqACXMatcher = reqACx.matcher(finals.lowercase())
                        val reqxxxMatcher = reqxxxx.matcher(finals.lowercase())
                        val reqcnoMatcher = reqacnoxx.matcher(finals.lowercase())
                        val reqacendingMatcher = reqacendingwith.matcher(finals.lowercase())
                        val reqacending2Matcher = reqacending.matcher(finals.lowercase())
                        val reqAcNoMatcher = reqAcNo.matcher(finals.lowercase())
                        val reqCardNoMatcher = reqCardNo.matcher(finals.lowercase())
                        val reqCardNo2Matcher = reqCardNo2.matcher(finals.lowercase())
                        val reqUpiNoMatcher = reqUpiNo.matcher(finals.lowercase())

                        val amountMatcherINR = amountINR.matcher(finals)
                        val amountMatcherINRDot = amountINRDot.matcher(finals)
                        val amountMatcherRs = amountRs.matcher(finals)
                        val amountMatcherRsSpace = amountRsSpace.matcher(finals)
                        val amountMatcherRsDot = amountRsDot.matcher(finals)
                        val amountMatcherRsDotSpace = amountRsDotSpace.matcher(finals)

                        val inrmatcher: Matcher = sbiPattern.matcher(body)
                        val transIDmatcher: Matcher = transIdPattern.matcher(body)
                        val qrmatcher: Matcher = paytmQRPattern.matcher(body)
                        val receivedFromPatternMatcher: Matcher = receivedFromPattern.matcher(body)
                        val receivedFromPatternMatcher2: Matcher = receivedFromPattern2.matcher(body)
                        val transferToPatternMatcher: Matcher = transferToPattern.matcher(body)
                        val transferToPatternMatcher2: Matcher = transferToPattern2.matcher(body)
                        val creditCardPatternMatcher: Matcher = creditCardPattern.matcher(body.lowercase())
                        val creditCardPatternMatcher2: Matcher = creditCardPattern2.matcher(body.lowercase())
                        val creditCardPatternMatcher3: Matcher = creditCardPattern3.matcher(body.lowercase())
                        val creditCardPatternMatcher4: Matcher = creditCardPattern4.matcher(body.lowercase())
                        val upimatcher: Matcher = upiIDPattern.matcher(body)
                        val atPatternMatcher: Matcher = atPattern.matcher(body)
                        val loanmatcher: Matcher = loanPattern.matcher(body)

                        var amount = ""
                        var upiId = ""
                        var accountNo = ""

                        if (reqACMatcher.find()) {
                            accountNo = reqACMatcher.group(1)
                        }
                        if (reqACDotMatcher.find()) {
                            accountNo = reqACDotMatcher.group(1) as String
                        }
                        if (reqACDotxMatcher.find()) {
                            accountNo = reqACDotxMatcher.group(1) as String
                        }
                        if (reqACXXMatcher.find()) {
                            accountNo = reqACXXMatcher.group(1) as String
                        }
                        if (reqACXMatcher.find()) {
                            accountNo = reqACXMatcher.group(1) as String
                        }
                        if (reqxxxMatcher.find()) {
                            accountNo = reqxxxMatcher.group(1) as String
                        }
                        if (reqcnoMatcher.find()) {
                            accountNo = reqcnoMatcher.group(1) as String
                        }
                        if (reqacendingMatcher.find()) {
                            accountNo = reqacendingMatcher.group(1) as String
                        }
                        if (reqacending2Matcher.find()) {
                            accountNo = reqacending2Matcher.group(1) as String
                        }
                        if (reqAcNoMatcher.find()) {
                            accountNo = reqAcNoMatcher.group(1) as String
                        }
                        if (reqCardNoMatcher.find()) {
                            accountNo = reqCardNoMatcher.group(1) as String
                        }
                        if (reqCardNo2Matcher.find()) {
                            accountNo = reqCardNo2Matcher.group(1) as String
                        }
                        if (reqUpiNoMatcher.find()) {
                            accountNo = reqUpiNoMatcher.group(1) as String
                        } else {
                            accountNo = "xx$accountNo"
                        }


                        if (inrmatcher.find()) {
                            //   amount = inrmatcher.group(1)
                        }
                        if (transIDmatcher.find()) {
                            upiId = transIDmatcher.group(1)
                        }
                        if (upimatcher.find()) {
                            upiId = upimatcher.group(1)
                        }
                        if (qrmatcher.find()) {
                            upiId = qrmatcher.group(1)
                        }
                        if (receivedFromPatternMatcher.find()) {
                            upiId = receivedFromPatternMatcher.group(0)
                        }
                        if (receivedFromPatternMatcher2.find()) {
                            upiId = receivedFromPatternMatcher2.group(0)
                        }
                        if (transferToPatternMatcher.find()) {
                            upiId = transferToPatternMatcher.group(0)
                        }
                        if (transferToPatternMatcher2.find()) {
                            upiId = transferToPatternMatcher2.group(0)
                        }
                        if (creditCardPatternMatcher.find()) {
                            upiId = creditCardPatternMatcher.group(0)
                        }
                        if (creditCardPatternMatcher2.find()) {
                            upiId = creditCardPatternMatcher2.group(0)
                        }
                        if (creditCardPatternMatcher3.find()) {
                            upiId = creditCardPatternMatcher3.group(0)
                        }
                        if (creditCardPatternMatcher4.find()) {
                            upiId = creditCardPatternMatcher4.group(0)
                        }
                        if (atPatternMatcher.find()) {
                            upiId = atPatternMatcher.group(0)
                        }

                        Log.d("BANK--", "getAllDeviceSms: $upiId")


                        if (amountMatcherINR.find()) {
                            amount = amountMatcherINR.group(1);
                        } else if (amountMatcherINRDot.find()) {
                            amount = amountMatcherINRDot.group(1);
                        } else if (amountMatcherRs.find()) {
                            amount = amountMatcherRs.group(1);
                        } else if (amountMatcherRsDot.find()) {
                            amount = amountMatcherRsDot.group(1);
                        } else if (amountMatcherRsDotSpace.find()) {
                            amount = amountMatcherRsDotSpace.group(1)
                        } else if (amountMatcherRsSpace.find()) {
                            amount = amountMatcherRsSpace.group(1)
                        }
                        if (amount.isNotEmpty()) {
                            lstSms.add(
                                TransactionModel(
                                    "",
                                    date,
                                    body,
                                    senderId,
                                    transactionType,
                                    amount,
                                    upiId,
                                    accountType,
                                    "",
                                    accountNo,
                                    ""
                                )
                            )
                        }

                    }
                }
            }


            return lstSms
        }

        fun checkTransactionSMS(title: String): String {
            return when {
                title.contains("pnb", ignoreCase = true) -> {
                    return "Punjab National Bank"
                }

                title.contains("paytm", ignoreCase = true) -> {
                    return "Paytm"
                }

                title.contains("hdfc", ignoreCase = true) -> {
                    return "HDFC Bank"
                }

                title.contains("slic", ignoreCase = true) -> {
                    return "Slice Card"
                }

                title.contains("axios", ignoreCase = true) -> {
                    return "Axis Bank"
                }

                title.contains("icic", ignoreCase = true) -> {
                    return "ICIC Bank"
                }

                title.contains("onecard", ignoreCase = true) -> {
                    return "One Card "
                }

                title.contains("sbi", ignoreCase = true) -> {
                    return "SBI Bank"
                }

                title.contains("axis", ignoreCase = true) -> {
                    return "Axis Bank"
                }

                title.contains("fed", ignoreCase = true) -> {
                    return "Federal Bank"
                }

                title.contains("idbi", ignoreCase = true) -> {
                    return "IDBI Bank"
                }


                else -> title


            }

        }

        fun getBankLogo(context: Context, bankName: String): Drawable {
            return when {
                bankName.contains("pnb", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.pnb_icon)
                }

                bankName.contains("punjab national bank", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.pnb_icon)
                }

                bankName.contains("paytm", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.paytm_icon)
                }

                bankName.contains("hdfc", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.hdfc_icon)
                }

                bankName.contains("slic", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.transaction_item_icon)
                }

                bankName.contains("axios", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.axis_icon)
                }

                bankName.contains("icic", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.icici_icon)
                }

                bankName.contains("onecard", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.transaction_item_icon)
                }

                bankName.contains("sbi", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.sbi_ico)
                }

                bankName.contains("axis", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.axis_icon)
                }

                bankName.contains("fed", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.federal_logo)
                }

                bankName.contains("idbi", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.idbi_icon)
                }

                bankName.contains("yesbank", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.yes_bank_logo)
                }

                bankName.contains("baroda", ignoreCase = true) -> {
                    return context.resources.getDrawable(R.drawable.bankofbaroda_logo)
                }


                else -> context.resources.getDrawable(R.drawable.transaction_item_icon)

            }

        }


        fun checkMesaage(body: String): Boolean {
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

        fun checkMesaageType(body: String): String {
            return when {
                body.contains("credited", ignoreCase = true) -> "Credit"
                body.contains("received", ignoreCase = true) && !body.contains("missed call", true) -> "Credit"
                body.contains("debited", ignoreCase = true) && body.contains("refunded", true) -> "Credit"
                else -> "Debit"
            }

        }

        fun checkAccountType(body: String): String {
            return when {
                body.contains("loan", ignoreCase = true) -> "Loan"
                body.contains("credit card", ignoreCase = true) -> "Credit Card"
                body.contains("card xx", ignoreCase = true) -> "Credit Card"
                else -> "Bank Account"
            }
        }

    }

}