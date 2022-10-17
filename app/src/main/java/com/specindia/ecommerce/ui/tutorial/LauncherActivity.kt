package com.specindia.ecommerce.ui.tutorial

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.spec.spec_ecommerce.databinding.ActivityLauncherBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.authentication.AuthActivity
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.ui.dashboard.viewmodel.DataViewModel
import com.specindia.ecommerce.util.Constants.Companion.SPLASH_SCREEN_TIME_OUT
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * when app open first load spalsh screen
 * also maintain after login which screen open
 */
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

    //when open screen get data in preference and manage which screen open
    private fun startLoadingContent() {
        val isOnBoardingFinished = dataStoreViewModel.getIsFirstTime()
        val isUserLoggedIn = dataStoreViewModel.getUserLoggedIn()

        val authActivity = AuthActivity::class.java
        val onBoardingActivity = OnBoardingActivity::class.java
        val homeAuthActivity = HomeActivity::class.java

        /**
         * isOnBoardingFinished default false
         * when user install app and first time open and complete step isOnBoardingFinished =true and save in preference
         */
        if (isOnBoardingFinished == true) {
            Handler(Looper.getMainLooper()).postDelayed({
                // when user are login redirect to home activity otherwise authentication activity like login screen
                if (isUserLoggedIn == true) {
                    startNewActivity(homeAuthActivity)
                } else {
                    startNewActivity(authActivity)
                }
            }, SPLASH_SCREEN_TIME_OUT)

        } else {
            //open tutorial screen
            Handler(Looper.getMainLooper()).postDelayed({
                startNewActivity(onBoardingActivity)
            }, SPLASH_SCREEN_TIME_OUT)
        }
    }
}