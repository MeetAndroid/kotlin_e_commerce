package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.specindia.ecommerce.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private val TAG = "Auth Activity"
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}