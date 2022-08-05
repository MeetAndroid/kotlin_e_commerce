package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.specindia.ecommerce.databinding.ActivityLauncherBinding
import com.specindia.ecommerce.ui.viewmodel.DataViewModel
import com.specindia.ecommerce.util.Constants.Companion.SPLASH_SCREEN_TIME_OUT
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding
    private val dataStoreViewModel by viewModels<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startLoadingContent()
    }

    private fun startLoadingContent() {
        val isOnBoardingFinished = dataStoreViewModel.getIsFirstTime()
        val isUserLoggedIn = dataStoreViewModel.getUserLoggedIn()

        val authActivity = AuthActivity::class.java
        val onBoardingActivity = OnBoardingActivity::class.java
        val homeAuthActivity = HomeActivity::class.java

        if (isOnBoardingFinished == true) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (isUserLoggedIn == true) {
                    startNewActivity(homeAuthActivity)
                } else {
                    startNewActivity(authActivity)
                }
            }, SPLASH_SCREEN_TIME_OUT)

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startNewActivity(onBoardingActivity)
            }, SPLASH_SCREEN_TIME_OUT)
        }
    }
}