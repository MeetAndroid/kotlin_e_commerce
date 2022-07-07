package com.specindia.ecommerce.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ActivityOnboardingBinding
import com.specindia.ecommerce.ui.adapters.OnBoardingViewPagerAdapter


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
        setUpViewPager()
    }

    private fun setFullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setUpViewPager() {
        val adapter =
            OnBoardingViewPagerAdapter(this@OnBoardingActivity,onBoardingContentList)
        binding.viewPager.adapter = adapter
    }

    data class OnBoardingData(val imageId: Int, val title: Int, val description: Int)

    private val onBoardingContentList = listOf(
        OnBoardingData(
            R.drawable.ic_find_food_you_love,
            R.string.on_boarding_title_1,
            R.string.on_boarding_description_1
        ),
        OnBoardingData(
            R.drawable.ic_fast_delivery,
            R.string.on_boarding_title_2,
            R.string.on_boarding_description_2
        ),
        OnBoardingData(
            R.drawable.ic_find_food_you_love,
            R.string.on_boarding_title_3,
            R.string.on_boarding_description_3
        )
    )
}