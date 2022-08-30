package com.specindia.ecommerce.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.specindia.ecommerce.databinding.FragmentSetLocationBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.CustomInfoWindowForGoogleMap
import com.specindia.ecommerce.util.PermissionUtils.isLocationEnabled
import com.specindia.ecommerce.util.PermissionUtils.locationRequest
import com.specindia.ecommerce.util.showShortToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


// ================== GOOGLE MAP
// https://www.geeksforgeeks.org/how-to-implement-current-location-button-feature-in-google-maps-in-android/

@AndroidEntryPoint
open class SetLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentSetLocationBinding
    private lateinit var data: AuthResponseData
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    // Google Map
    private var mapView: MapView? = null
    private lateinit var googleMap: GoogleMap

    private var latitude = 0.0
    private var longitude = 0.0
    var currentLocation: LatLng = LatLng(0.0, 0.0)
    private var fullAddress = ""

    private var isPermissionON: Boolean = false


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                Log.d("TAG", "Precise location access granted.")
                isPermissionON = true

            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Log.d("TAG", "Only approximate location access granted.")
                isPermissionON = true
            }
            else -> {
                // No location access granted.
                Log.d("TAG", "No location access granted.")
                isPermissionON = false
            }
        }

        if (isPermissionON) {
            if (!(activity as HomeActivity).isGpsON) {
                (activity as HomeActivity).enableGPS()
            } else {
                showDataOnMap()
                binding.ivLocation.setImageResource(com.specindia.ecommerce.R.drawable.ic_location_enable)
            }
        } else {
            showLocationPermissionDialogOnDenied((activity as HomeActivity))
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
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(),
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
            LocationServices.getFusedLocationProviderClient(requireActivity())

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

        (activity as HomeActivity).homeViewModel.gpsStatus.observe(viewLifecycleOwner
        ) { gpsStatus ->
            if (gpsStatus) {
                Log.d("TAG", "Observer")
                showDataOnMap()
                binding.ivLocation.setImageResource(com.specindia.ecommerce.R.drawable.ic_location_enable)
            } else {
                binding.ivLocation.setImageResource(com.specindia.ecommerce.R.drawable.ic_location_disable)
            }
        }
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
            super.onLocationResult(locationResult)

            val mLastLocation: Location? = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation!!.latitude, mLastLocation.longitude)
            Log.d("TAG ", "Current Location$currentLocation")
            showDataOnMap()
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        mFusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun showDataOnMap() {

        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val location: Location? = task.result
                if (location == null) {
                    Log.d("TAG", "Location Null")
                    requestNewLocationData()
                } else {
                    Log.d("TAG", "Location Not Null")
                    currentLocation = LatLng(location.latitude, location.longitude)
                    Log.d("TAG", "update time ${System.currentTimeMillis()}")
                    Log.d("currentLocation", "currentLocation$currentLocation")
                    googleMap.clear()
                    if (isPermissionON) {
                        //googleMap.isMyLocationEnabled = true
                    }
                    if (location.latitude != 0.0 && location.longitude != 0.0) {
                        val marker: Marker? =
                            googleMap.addMarker(MarkerOptions().position(currentLocation)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                            16F))

                        googleMap.setOnCameraMoveListener {
                            val midLatLng = googleMap.cameraPosition.target
                            if (marker != null) {
                                marker.position = midLatLng
                                val nowLocation = marker.position
                                Log.d("TAG", "now Location$nowLocation")

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
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } else {
                requireActivity().showShortToast("No current location found")
                Log.d("TAG", "No current location found")
            }

        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mMap: GoogleMap) {
        Log.d("TAG", "onMapReady")
        googleMap = mMap

        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(requireActivity()))


        if (isPermissionON) {
            if (!(activity as HomeActivity).isGpsON) {
                binding.ivLocation.setImageResource(com.specindia.ecommerce.R.drawable.ic_location_disable)
                (activity as HomeActivity).enableGPS()
            } else {
                showDataOnMap()
                binding.ivLocation.setImageResource(com.specindia.ecommerce.R.drawable.ic_location_enable)
            }
        } else {
            requestLocationPermission()
        }

        binding.ivLocation.setOnClickListener {
            if (isPermissionON) {
                if (!(activity as HomeActivity).isGpsON) {
                    binding.ivLocation.setImageResource(com.specindia.ecommerce.R.drawable.ic_location_disable)
                    (activity as HomeActivity).enableGPS()
                } else {
                    showDataOnMap()
                }
            } else {
                requestLocationPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        Log.d("TAG", "onResume")
        (activity as HomeActivity).isGpsON = isLocationEnabled(requireActivity())
        isPermissionON = checkPermissions()
        Log.d("TAG", "GPS status ${(activity as HomeActivity).isGpsON}")
        Log.d("TAG", "Permission status $isPermissionON")

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
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
        Log.d("TAG", "onLowMemory")
    }


    private fun getAndSetAddressOnTextView(location: GeoPoint, marker: Marker?) {
        var addressList: ArrayList<Address> = ArrayList()
        fullAddress = ""
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
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
                    Log.d("TAG", "Full Address$fullAddress")
                    marker?.title = fullAddress
                    marker?.showInfoWindow()
                }
            }
        }

    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
            }
        }

    private fun showLocationPermissionDialogOnDenied(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(com.specindia.ecommerce.R.string.app_name))
            .setMessage("Enable location permission to set the Shipping Address")
            .setCancelable(false)
            .setPositiveButton(context.getString(com.specindia.ecommerce.R.string.ok)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                resultLauncher.launch(intent)
            }
            .show()
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

}