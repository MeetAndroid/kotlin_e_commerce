package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.specindia.ecommerce.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private val TAG = "Home Activity"
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}