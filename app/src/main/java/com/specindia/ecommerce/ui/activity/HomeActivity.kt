package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.specindia.ecommerce.databinding.ActivityHomeBinding
import com.specindia.ecommerce.util.startNewActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private val TAG = "Home Activity"
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun performLogout() = lifecycleScope.launch {
//        viewModel.logout()
//        userPreferences.clear()
        startNewActivity(AuthActivity::class.java)
    }
}