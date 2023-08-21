package com.moreyeahs.financeapp.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.dashboard.MainActivity
import com.moreyeahs.financeapp.presentation.onboarding.OnBoardingActivity
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val iv: ImageView = findViewById(R.id.iv_splash_logo)
        iv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))
    }

    override fun onResume() {
        super.onResume()

        var intent = Intent(this, AuthUserActivity::class.java)

        lifecycleScope.launch {
            intent = if (prefs.getBoolean(Constants.IS_USER_FIRST_TIME) == false) {
                if (prefs.getBoolean(Constants.IS_USER_LOGGED_IN) == true) {
                    Intent(this@SplashActivity, MainActivity::class.java)
                } else {
                    Intent(this@SplashActivity, AuthUserActivity::class.java)
                }
            } else {
                Intent(this@SplashActivity, OnBoardingActivity::class.java)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }
}