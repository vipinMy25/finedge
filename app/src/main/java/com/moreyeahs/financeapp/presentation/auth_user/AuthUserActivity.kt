package com.moreyeahs.financeapp.presentation.auth_user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.presentation.auth_user.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_user)

        loadFragment(LoginFragment())
    }

    fun loadFragment(fragment: Fragment) {
        /*if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }*/

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_auth_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}