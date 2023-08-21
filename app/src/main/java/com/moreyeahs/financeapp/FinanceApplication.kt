package com.moreyeahs.financeapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinanceApplication : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: FinanceApplication? = null

        fun getApplicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}