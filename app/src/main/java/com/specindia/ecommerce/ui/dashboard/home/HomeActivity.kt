package com.specindia.ecommerce.ui.dashboard.home

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.ActivityHomeBinding
import com.specindia.ecommerce.datastore.abstraction.DataStoreRepository
import com.specindia.ecommerce.models.FavRestaurants
import com.specindia.ecommerce.ui.authentication.AuthActivity
import com.specindia.ecommerce.ui.checkout.address.SetLocationFragment
import com.specindia.ecommerce.ui.dashboard.viewmodel.DataViewModel
import com.specindia.ecommerce.ui.home.HomeFragment
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    var favRestaurantArray = ArrayList<FavRestaurants>()

    val dataStoreViewModel by viewModels<DataViewModel>()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    val homeViewModel by viewModels<HomeViewModel>()
    var isGpsON: Boolean = false

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
            val isHomeTabSelected =
                binding.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked

            if (isHomeTabSelected) {
                if (getCurrentFragmentInstance(this) is HomeFragment) {
                    showShortToast(getString(R.string.msg_already_in_home_screen))
                } else {

                    /**
                     * If user is in Home Tab but not in Home Fragment, and click Fab button
                     * then remove all fragment and go to HomeFragment
                     */

                    val navOption: NavOptions =
                        NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build()
                    this.navController.navigate(R.id.homeFragment, null, navOption)
                }
            } else {
                // If user is not in Home tab and click Fab button then we load the Home Tab
                binding.bottomNavigationView.menu.performIdentifierAction(R.id.nav_home, 0)
            }

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

    // ================= location related code

    fun enableGPS() {

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(PermissionUtils.locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(
            this
        )
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                Log.d("TAG", "addOnCompleteListener: $response")
                if (response.locationSettingsStates?.isLocationPresent == true) {
                    isGpsON = true
                    homeViewModel.setGpsStatus(true)
                    Toast.makeText(
                        this,
                        "GPS is already turned on",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        Log.d("TAG", "RESOLUTION_REQUIRED")

                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this,
                            Constants.REQUEST_CHECK_SETTINGS
                        )
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.d("TAG", "SETTINGS_CHANGE_UNAVAILABLE: No location")
                        Toast.makeText(
                            this,
                            "Device does not have location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    // onActivityResult deprecated. Solution is registerForActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CHECK_SETTINGS) {

            when (resultCode) {

                RESULT_OK -> {
                    Log.d("TAG", "RESULT_OK: GPS ON")
                    isGpsON = true

                    if ((this.navController.currentDestination as FragmentNavigator.Destination).className == SetLocationFragment::class.java.canonicalName
                    ) {
                        Log.d("TAG", "CheckStatus In Home Activity OK")
                        homeViewModel.setGpsStatus(true)
                    }
                }
                RESULT_CANCELED -> {
                    Log.d("TAG", "RESULT_CANCELED: GPS OFF")
                    isGpsON = false

                    if ((this.navController.currentDestination as FragmentNavigator.Destination).className == SetLocationFragment::class.java.canonicalName
                    ) {
                        Log.d("TAG", "CheckStatus In Home Activity OK")
                        homeViewModel.setGpsStatus(false)
                    }
                }
            }
        }
    }
}
