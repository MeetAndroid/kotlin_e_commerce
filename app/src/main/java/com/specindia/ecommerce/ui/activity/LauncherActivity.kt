package com.specindia.ecommerce.ui.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.specindia.ecommerce.databinding.ActivityLauncherBinding
import com.specindia.ecommerce.util.startNewActivity


class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoadingContent()
    }

    private fun startLoadingContent() {
        // For this example, the Timer delay represents awaiting a response from a network call
        val activity = OnBoardingActivity::class.java
        Handler(Looper.getMainLooper()).postDelayed({
            startNewActivity(activity)
        }, 3000)
    }

}