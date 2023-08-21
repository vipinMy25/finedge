package com.moreyeahs.financeapp.presentation.side_navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.presentation.side_navigation.feedback.FeedbackFragment
import com.moreyeahs.financeapp.presentation.side_navigation.profile.EditProfileFragment
import com.moreyeahs.financeapp.presentation.side_navigation.web_page.WebPageFragment
import com.moreyeahs.financeapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SideNavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_navigation)

        initView()
    }

    private fun initView() {
        val screen = intent.getStringExtra("Screen")
        screen?.let {
            when (it) {
                "Profile" -> {
                    loadFragment(EditProfileFragment())
                }

                "PrivacyPolicy" -> {
                    val bundle = Bundle()
                    bundle.putString("WebUrl", Constants.PRIVACY_POLICY_URL)
                    bundle.putString("Title", "Privacy Policy")
                    val fragment = WebPageFragment()
                    fragment.arguments = bundle
                    loadFragment(fragment)
                }

                "TermsCondition" -> {
                    val bundle = Bundle()
                    bundle.putString("WebUrl", Constants.TERMS_CONDITION_URL)
                    bundle.putString("Title", "Terms & Conditions")
                    val fragment = WebPageFragment()
                    fragment.arguments = bundle
                    loadFragment(fragment)
                }

                "feedback" -> {
                    val fragment = FeedbackFragment()
                    loadFragment(fragment)
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_side_navigation_container, fragment)
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