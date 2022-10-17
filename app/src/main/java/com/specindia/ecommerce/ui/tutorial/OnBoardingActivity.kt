package com.specindia.ecommerce.ui.tutorial

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.ActivityOnboardingBinding
import com.specindia.ecommerce.ui.authentication.AuthActivity
import com.specindia.ecommerce.ui.dashboard.viewmodel.DataViewModel
import com.specindia.ecommerce.ui.tutorial.adapter.OnBoardingViewPagerAdapter
import com.specindia.ecommerce.util.startNewActivity
import com.specindia.ecommerce.util.visible
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint

/**
 * This fragment show app overview for brief describe in three steps
 */
@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private val dataStoreViewModel by viewModels<DataViewModel>()

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
        setUpViewPager()
        setNextPreviousClick()
    }

    // set the step in view pager for change content and images
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

                    } else {
                        tvNext.text = getString(R.string.next)
                    }
                }

            })
        }

    }

    // There are define two text Previous and Next and click to text show next previous screen
    private fun setNextPreviousClick() {
        binding.apply {
            tvPrevious.setOnClickListener {
                viewPager.setCurrentItem(getItemOfViewPager(-1), true)
            }

            tvNext.setOnClickListener {
                if (tvNext.text.equals(getString(R.string.finish))) {
                    dataStoreViewModel.saveIsFirstTime(true)
                    startNewActivity(AuthActivity::class.java)
                } else {
                    dataStoreViewModel.saveIsFirstTime(false)
                    viewPager.setCurrentItem(getItemOfViewPager(+1), true)
                }

            }
        }
    }

    // get the current show item viewpager
    private fun getItemOfViewPager(i: Int): Int {
        return binding.viewPager.currentItem + i
    }
}