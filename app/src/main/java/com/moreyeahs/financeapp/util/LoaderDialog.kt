package com.moreyeahs.financeapp.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.moreyeahs.financeapp.R

class LoaderDialog(val context: Context) {

    private val dialog = Dialog(context)

    fun showLoaderDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loader)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }

}