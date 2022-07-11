package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ActivityHomeBinding
import com.specindia.ecommerce.ui.viewmodel.DataViewModel
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val TAG = "Home Activity"
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel by viewModels<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modifyBottomNavigationView()
        setUpFabClick()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navControllerView) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Setup the ActionBar with navController and 3 top level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.foodMenuFragment,
                R.id.offersFragment,
                R.id.profileFragment,
                R.id.moreFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun setUpFabClick() {
        binding.fabAdd.setOnClickListener {
            Toast.makeText(this@HomeActivity, "Home Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun modifyBottomNavigationView() {
        binding.apply {
            bottomNavigationView.background = null
            bottomNavigationView.menu[2].isEnabled = false
        }
    }

    fun performLogout() = lifecycleScope.launch {

        viewModel.saveUserLoggedIn(false)
        startNewActivity(AuthActivity::class.java)
    }
}