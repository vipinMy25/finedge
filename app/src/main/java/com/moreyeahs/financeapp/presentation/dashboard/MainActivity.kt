package com.moreyeahs.financeapp.presentation.dashboard

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.ActivityMainBinding
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.presentation.dashboard.account.AllAccountFragment
import com.moreyeahs.financeapp.presentation.dashboard.home.HomeFragment
import com.moreyeahs.financeapp.presentation.dashboard.report.ReportsFragment
import com.moreyeahs.financeapp.presentation.side_navigation.SideNavigationActivity
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.LoaderDialog
import com.moreyeahs.financeapp.util.PreferencesManager
import com.moreyeahs.financeapp.util.Utils
import com.moreyeahs.financeapp.util.logoutUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var prefs: PreferencesManager

    @Inject
    lateinit var financeRepo: FinanceRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        ) {
            val loader = LoaderDialog(this)
            loader.showLoaderDialog()
            lifecycleScope.launch(Dispatchers.IO) {
                val syncDatabase = viewModel.syncDatabase(this@MainActivity, this@MainActivity)
                syncDatabase.join()
                loader.dismissDialog()
                withContext(Dispatchers.Main) {
                    initViews()
                }
            }
        } else {
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS
            )

            ActivityCompat.requestPermissions(this, permissions, 1)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val loader = LoaderDialog(this)
                loader.showLoaderDialog()
                lifecycleScope.launch(Dispatchers.IO) {
                    val syncDatabase = viewModel.syncDatabase(this@MainActivity, this@MainActivity)
                    syncDatabase.join()
                    loader.dismissDialog()
                    withContext(Dispatchers.Main) {
                        initViews()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val name = prefs.getString(Constants.USER_NAME)
            val mobile = prefs.getString(Constants.USER_MOBILE_NUMBER)
            binding.toolbar.tvToolbarTitle.text = if (name?.isNotEmpty() == true) name else "FinUser"
            binding.toolbar.tvToolbarDescription.text = if (mobile?.isNotEmpty() == true) mobile else Utils.getCurrentPhoneNumber(this@MainActivity)

            binding.tvUserName.text = if (name?.isNotEmpty() == true) name else "FinUser"
            binding.tvUserMobile.text = if (mobile?.isNotEmpty() == true) mobile else Utils.getCurrentPhoneNumber(this@MainActivity)
        }
    }

    private fun initViews() {
        loadFragment(HomeFragment())

        val display = getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)
        val size = Point()
        display!!.getSize(size)
        val displayWidth = (size.x * 0.9f).toDouble()
        val translateTo = displayWidth * .90

        binding.toolbar.ivNavAction.setOnClickListener {
            openSideNavigation(translateTo)
        }

        binding.ivClose.setOnClickListener {
            closeSideNavigation()
        }

        binding.bnvHome.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    binding.toolbar.ivMenuAction.visibility = View.VISIBLE
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_account -> {
                    binding.toolbar.ivMenuAction.visibility = View.GONE
                    loadFragment(AllAccountFragment())
                    true
                }

                R.id.nav_report -> {
                    binding.toolbar.ivMenuAction.visibility = View.GONE
                    loadFragment(ReportsFragment())
                    true
                }

                else -> false
            }
        }

        binding.rlUserProfile.setOnClickListener {
            closeSideNavigation()
            val intent = Intent(this, SideNavigationActivity::class.java)
            intent.putExtra("Screen", "Profile")
            startActivity(intent)
        }

        binding.llFeedbackMenu.setOnClickListener {
            closeSideNavigation()
            val intent = Intent(this, SideNavigationActivity::class.java)
            intent.putExtra("Screen", "feedback")
            startActivity(intent)
        }

        binding.llAboutMenu.setOnClickListener {
            Toast.makeText(this, "Coming soon ...", Toast.LENGTH_SHORT).show()
        }

        binding.llPrivacyPolicyMenu.setOnClickListener {
            closeSideNavigation()
            val intent = Intent(this, SideNavigationActivity::class.java)
            intent.putExtra("Screen", "PrivacyPolicy")
            startActivity(intent)
        }

        binding.llTermsConditionsMenu.setOnClickListener {
            closeSideNavigation()
            val intent = Intent(this, SideNavigationActivity::class.java)
            intent.putExtra("Screen", "TermsCondition")
            startActivity(intent)
        }

        binding.llLogoutMenu.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                logoutUser(prefs, financeRepo, this@MainActivity, this@MainActivity, false)
            }
        }
    }

    private fun openSideNavigation(translateTo: Double) {
        binding.rlParentMain.animate().translationX(translateTo.toFloat())
            .scaleY(0.95f).setDuration(500).scaleX(0.95f)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()

        binding.llSideNavigation.isEnabled = false

        applyCurve()
    }

    private fun applyCurve() {
        val curveRadius = 40F

        binding.rlParentMain.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(
                    0,
                    0,
                    view!!.width,
                    (view.height + curveRadius).toInt(),
                    curveRadius
                )
            }
        }

        binding.rlParentMain.clipToOutline = true
    }

    private var sideNavAnimationListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }

    private fun closeSideNavigation() {
        binding.rlParentMain.animate().translationX(0F).scaleY(1f).scaleX(1f).setDuration(500).setListener(sideNavAnimationListener).start()
        binding.toolbar.ivNavAction.visibility = View.VISIBLE
        binding.llSideNavigation.isEnabled = true
        removeCurve()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun removeCurve() {
        val curveRadius = 0F
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.rlParentMain.outlineProvider = object : ViewOutlineProvider() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(
                        0,
                        0,
                        view!!.width,
                        (view.height + curveRadius).toInt(),
                        curveRadius
                    )
                }
            }
            binding.rlParentMain.clipToOutline = true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main_container, fragment)
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