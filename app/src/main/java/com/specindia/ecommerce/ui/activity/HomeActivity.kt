package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ActivityHomeBinding
import com.specindia.ecommerce.models.FavRestaurants
import com.specindia.ecommerce.ui.viewmodel.DataViewModel
import com.specindia.ecommerce.ui.viewmodel.HomeViewModel
import com.specindia.ecommerce.util.HideListenableBottomAppBarBehavior
import com.specindia.ecommerce.util.logoutFromFacebook
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    var favRestaurantArray = ArrayList<FavRestaurants>()

    val dataStoreViewModel by viewModels<DataViewModel>()
    val homeViewModel by viewModels<HomeViewModel>()

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
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun setUpFabClick() {
        binding.fabAdd.setOnClickListener {
            binding.bottomNavigationView.menu.performIdentifierAction(R.id.nav_home, 0)
        }
    }

    private fun modifyBottomNavigationView() {
        binding.apply {
            bottomNavigationView.background = null
        }
    }

    fun performLogout() = lifecycleScope.launch {
        logoutFromFacebook()
        dataStoreViewModel.saveUserLoggedIn(false)
        dataStoreViewModel.clearPreferences()
        dataStoreViewModel.saveIsFirstTime(true)
        startNewActivity(AuthActivity::class.java)
    }

    fun showOrHideBottomAppBarAndFloatingActionButtonOnScroll() {
        val params = binding.bottomAppBar.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = object : HideListenableBottomAppBarBehavior() {
            override fun onSlideDown() {
                binding.fabAdd.hide()
            }

            override fun onSlideUp() {
                binding.fabAdd.show()
            }
        }
    }
}
