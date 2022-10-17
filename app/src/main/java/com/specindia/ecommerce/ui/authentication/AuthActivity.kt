package com.specindia.ecommerce.ui.authentication


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.spec.spec_ecommerce.databinding.ActivityAuthBinding
import com.specindia.ecommerce.datastore.abstraction.DataStoreRepository
import com.specindia.ecommerce.ui.dashboard.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * This activity load to authentication fragment
 */
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val TAG = "Auth Activity"
    private lateinit var binding: ActivityAuthBinding

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    val dataStoreViewModel by viewModels<DataViewModel>()
    val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}