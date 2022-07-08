package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.specindia.ecommerce.databinding.ActivityHomeBinding
import com.specindia.ecommerce.ui.viewmodel.DataViewModel
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val TAG = "Home Activity"
    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun performLogout() = lifecycleScope.launch {

        viewModel.saveUserLoggedIn(false)
        startNewActivity(AuthActivity::class.java)
    }
}