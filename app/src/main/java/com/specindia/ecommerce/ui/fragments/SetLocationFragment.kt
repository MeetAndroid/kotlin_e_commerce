package com.specindia.ecommerce.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentSetLocationBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.CustomInfoWindowForGoogleMap
import com.specindia.ecommerce.util.showShortToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

// ================== GOOGLE MAP
// https://www.geeksforgeeks.org/how-to-implement-current-location-button-feature-in-google-maps-in-android/

@AndroidEntryPoint
class SetLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentSetLocationBinding
    private lateinit var data: AuthResponseData
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    // Google Map
    private var mapView: MapView? = null
    private lateinit var googleMap: GoogleMap

    private var latitude = 37.34199218751879
    private var longitude = -121.99989950773337
    var currentLocation: LatLng = LatLng(20.5, 78.9)
    private var fullAddress = ""

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val isPermissionGranted: Boolean
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                Log.d("TAG", "Precise location access granted.")
                isPermissionGranted = true

            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Log.d("TAG", "Only approximate location access granted.")
                isPermissionGranted = true
            }
            else -> {
                // No location access granted.
                Log.d("TAG", "No location access granted.")
                isPermissionGranted = false
            }
        }
        if (isPermissionGranted) {
            getLastLocation()
        }
    }

    /**
    Before you perform the actual permission request, check whether your app
    already has the permissions, and whether your app needs to show a permission
    rationale dialog. For more details, see Request permissions.
     * */
    private fun requestLocationPermission() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    // Check if location permissions are granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission((activity as HomeActivity),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission((activity as HomeActivity),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("TAG", "onSaveInstanceState")
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d("TAG", "onCreateView")
        binding = FragmentSetLocationBinding.inflate(layoutInflater)

        // Google Map
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        // Initializing fused location client
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient((activity as HomeActivity))

        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated")
        setUpHeader()
        setUpHeaderItemClick()
        setUpButtonClick()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
    }

    private fun setUpHeader() {
        with(binding) {
            with(addNewLocationScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(com.specindia.ecommerce.R.string.select_location)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                frShoppingCart.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(addNewLocationScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }

    private fun setUpButtonClick() {
        with(binding) {
            btnNext.setOnClickListener {
                it.findNavController()
                    .navigate(SetLocationFragmentDirections.actionSetLocationFragmentToAddAddressFragment(
                        fullAddress = fullAddress,
                        latitude = latitude.toString(),
                        longitude = longitude.toString()))
            }
        }
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation!!.latitude, mLastLocation.longitude)
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(5000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(100);

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient((activity as HomeActivity))
        mFusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        // if (isLocationEnabled()) {
        Log.d("IN", "== 1 ")
        if (checkPermissions()) {
            Log.d("IN", "== 2 ")
            mFusedLocationClient.lastLocation.addOnCompleteListener((activity as HomeActivity)) { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    googleMap.clear()
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    googleMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap((activity as HomeActivity)))
                    val marker: Marker? =
                        googleMap.addMarker(MarkerOptions().position(currentLocation))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                        16F))

                    googleMap.setOnMyLocationButtonClickListener {
                        requireActivity().showShortToast("Current Location clicked")
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            16F))
                        true
                    }

                    googleMap.setOnCameraMoveListener {
                        val midLatLng = googleMap.cameraPosition.target
                        if (marker != null) {
                            marker.position = midLatLng
                            val nowLocation = marker.position
                            Log.d("nowLocation", nowLocation.toString())

                            latitude = nowLocation.latitude
                            longitude = nowLocation.longitude
                        }
                    }

                    googleMap.setOnCameraIdleListener {
                        try {
                            if (latitude != 0.0 && longitude != 0.0) {
                                getAndSetAddressOnTextView(GeoPoint(latitude, longitude),
                                    marker)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }
            }
        } else {
            Log.d("IN", "== 3 ")
            requestLocationPermission()
        }
//        } else {
//            Log.d("IN", "== 4 ")
//            showGPSNotEnabledDialog((activity as HomeActivity))
//        }

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mMap: GoogleMap) {
        Log.d("TAG", "onMapReady")
        googleMap = mMap
        showGPSNotEnabledDialog((activity as HomeActivity))
        //enableGPS()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        Log.d("TAG", "onResume")
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        Log.d("TAG", "onPause")
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        Log.d("TAG", "onStart")
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        Log.d("TAG", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        Log.d("TAG", "onDestroy")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
        Log.d("TAG", "onLowMemory")
    }


    private fun getAndSetAddressOnTextView(location: GeoPoint, marker: Marker?) {
        var addressList: ArrayList<Address> = ArrayList()
        fullAddress = ""
        val geocoder = Geocoder((activity as HomeActivity), Locale.getDefault())
        CoroutineScope(Dispatchers.IO).launch {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(location.latitude,
                    location.longitude,
                    1
                ) { addresses -> addressList = addresses as ArrayList<Address> }
            } else {

                addressList = geocoder.getFromLocation(location.latitude,
                    location.longitude,
                    1
                ) as ArrayList<Address>
            }

            withContext(Dispatchers.Main) {
                if (addressList.size > 0) {
                    fullAddress = addressList[0].getAddressLine(0)
                    //binding.tvAddress.text = fullAddress
                    Log.d("fullAddress", fullAddress)
                    marker?.title = fullAddress
                    marker?.showInfoWindow()
                }
            }
        }

    }

    private fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.app_name))
            .setMessage("GPS required for getting Location")
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
//                if (!isLocationEnabled()) {
//                    enableGPS()
//                } else {
//                    getLastLocation()
//                }
                enableGPS()
                getLastLocation()

            }
            .show()
    }

    private fun enableGPS() {
        val locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(5000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(100);
        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
        locationSettingsRequestBuilder.addLocationRequest(locationRequest)
        locationSettingsRequestBuilder.setAlwaysShow(true)

        val settingClient = LocationServices.getSettingsClient((activity as HomeActivity))
        val task = settingClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        task.addOnSuccessListener((activity as HomeActivity)) { locationSettingResponse ->
            requireActivity().showShortToast("GPS Enabled ${locationSettingResponse.locationSettingsStates.toString()}")
            Log.d("HI", "Location ON")
            getLastLocation()
        }

        task.addOnFailureListener((activity as HomeActivity)) { exception ->
            Log.d("HI", "Location OFF")
            getLastLocation()
            when ((exception as ApiException).statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    // Show the dialog by calling startResolutionForResult(), and check the
                    // result in onActivityResult().
                    val rae = exception as ResolvableApiException
                    rae.startResolutionForResult((activity as HomeActivity),
                        Constants.GPS_REQUEST)
                } catch (sie: IntentSender.SendIntentException) {
                    Log.i(ContentValues.TAG,
                        "PendingIntent unable to execute request.")
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    val errorMessage = "Location settings are inadequate, and cannot be " +
                            "fixed here. Fix in Settings."
                    Log.e(ContentValues.TAG, errorMessage)

                    requireActivity().showShortToast(errorMessage)
                }
            }
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            (activity as HomeActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    // Use this method if we have to use our own drawable for marker
    private fun getBitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight)

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                Log.d("RESULT_OK", "GPS ON")
            }
        }
    }

}