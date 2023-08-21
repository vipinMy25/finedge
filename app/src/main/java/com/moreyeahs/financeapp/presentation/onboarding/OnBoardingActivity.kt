package com.moreyeahs.financeapp.presentation.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.ActivityOnBoardingBinding
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.util.Constants.IS_USER_FIRST_TIME
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private val viewModel by viewModels<OnBoardingViewModel>()
    private lateinit var onOnBoardingAdapter: OnBoardingAdapter
    private var currentPosotion = 0

    @Inject
    lateinit var prefs: PreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

        viewModel.onBoardingItemObserver.observe(this) {
            setUpViewPager(it)
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            prefs.putBoolean(IS_USER_FIRST_TIME, false)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.llDots.childCount
        for (i in 0 until childCount) {
            val imageView = binding.llDots[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_indicator_inactive
                    )
                )
            }
        }
        if (index == onOnBoardingAdapter.itemCount - 1) {
            binding.tvSkip.visibility = View.INVISIBLE
            binding.btnNext.text = "Finish"
        } else {
            binding.tvSkip.visibility = View.VISIBLE
            binding.btnNext.text = "Next"
        }
        binding.btnNext.setOnClickListener {
            if (index == onOnBoardingAdapter.itemCount - 1) {
                val intent = Intent(this, AuthUserActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                currentPosotion += 1
                binding.vpOnBoarding.setCurrentItem(currentPosotion, true)
            }
        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(this, AuthUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setUpIndicator() {
        val indicators = arrayOfNulls<ImageView>(onOnBoardingAdapter.itemCount)
        val layoutParams: LinearLayoutCompat.LayoutParams = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 10, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity, R.drawable.ic_indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.llDots.addView(indicators[i])
        }
    }

    private fun setUpViewPager(it: ArrayList<OnBoardingItemModel>?) {
        onOnBoardingAdapter = OnBoardingAdapter(this, it!!)
        binding.vpOnBoarding.adapter = onOnBoardingAdapter

        binding.vpOnBoarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

            }
        })
        setUpIndicator()
        setCurrentIndicator(0)
    }
}