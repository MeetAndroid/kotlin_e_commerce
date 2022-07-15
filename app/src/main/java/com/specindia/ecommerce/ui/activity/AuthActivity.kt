package com.specindia.ecommerce.ui.activity


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.specindia.ecommerce.databinding.ActivityAuthBinding
import com.specindia.ecommerce.ui.viewmodel.AuthViewModel
import com.specindia.ecommerce.ui.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val TAG = "Auth Activity"
    private lateinit var binding: ActivityAuthBinding

    val dataStoreViewModel by viewModels<DataViewModel>()
    val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}