package com.specindia.ecommerce.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ActivityOnboardingBinding
import com.specindia.ecommerce.ui.adapters.OnBoardingViewPagerAdapter
import com.specindia.ecommerce.util.startNewActivity
import com.specindia.ecommerce.util.visible
import com.zhpan.indicator.enums.IndicatorStyle


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private var hasFinishClicked: Boolean = false

    data class OnBoardingData(val imageId: Int, val title: Int, val description: Int)


    private val onBoardingContentList = listOf(
        OnBoardingData(
            R.drawable.ic_find_food_you_love,
            R.string.on_boarding_title_1,
            R.string.on_boarding_description_1
        ),
        OnBoardingData(
            R.drawable.ic_live_tracking,
            R.string.on_boarding_title_2,
            R.string.on_boarding_description_2
        ),
        OnBoardingData(
            R.drawable.ic_delivery_vector,
            R.string.on_boarding_title_3,
            R.string.on_boarding_description_3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setFullScreen()
        setUpViewPager()
        setNextPreviousClick()
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
            OnBoardingViewPagerAdapter(this@OnBoardingActivity, onBoardingContentList)
        binding.apply {
            viewPager.adapter = adapter
            indicatorView.setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            indicatorView.setupWithViewPager(viewPager)

            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d("Selected_Page", position.toString())
                    if (position != 0) {
                        tvPrevious.visible(true)
                    } else {
                        tvPrevious.visible(false)
                    }

                    if (position == 2) {
                        tvNext.text = getString(R.string.finish)
                        hasFinishClicked = true

                    } else {
                        tvNext.text = getString(R.string.next)
                    }
                }

            })
        }

    }

    private fun setNextPreviousClick() {
        binding.apply {
            tvPrevious.setOnClickListener {
                viewPager.setCurrentItem(getItemOfViewPager(-1), true)
            }

            tvNext.setOnClickListener {
                if (hasFinishClicked) {
                    startNewActivity(AuthActivity::class.java)
                } else {
                    viewPager.setCurrentItem(getItemOfViewPager(+1), true)
                }

            }
        }
    }

    private fun getItemOfViewPager(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

}