package com.specindia.ecommerce.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.specindia.ecommerce.databinding.ActivityOnboardingBinding
import com.specindia.ecommerce.util.startNewActivity

class OnBoardingActivity : AppCompatActivity() {
    private val TAG = "On Boarding Activity"
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.btnNext.setOnClickListener {
            startNewActivity(AuthActivity::class.java)
        }
    }

}