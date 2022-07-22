package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.specindia.ecommerce.databinding.ActivityDataStoreBinding
import com.specindia.ecommerce.datastore.DataStoreUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DataStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataStoreBinding

    @Inject
    lateinit var dataStore: DataStoreUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btFetchData.setOnClickListener {
            lifecycleScope.launch {
                dataStore.getData().collect {
                    binding.tvData.text = it
                }
            }
        }

        binding.btStoreData.setOnClickListener {
            lifecycleScope.launch {
                dataStore.setData(binding.etData.text.toString())
            }
        }

        binding.btFetchSecuredData.setOnClickListener {
            lifecycleScope.launch {
                dataStore.getSecuredData().collect {
                    binding.tvSecuredData.text = it
                }
            }
        }

        binding.btStoreSecuredData.setOnClickListener {
            lifecycleScope.launch {
                dataStore.setSecuredData(binding.etSecuredData.text.toString())
            }
        }
    }
}