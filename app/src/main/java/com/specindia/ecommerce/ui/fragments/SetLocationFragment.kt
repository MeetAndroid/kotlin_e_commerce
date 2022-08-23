package com.specindia.ecommerce.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.specindia.ecommerce.databinding.FragmentSetLocationBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.CustomInfoWindowForGoogleMap
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


@AndroidEntryPoint
class SetLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentSetLocationBinding
    private lateinit var data: AuthResponseData

    // Google Map
    private var mMap: MapView? = null
    private var latitude = 37.34199218751879
    private var longitude = -121.99989950773337
    private var fullAddress = ""


    //    private var locationCallback: LocationCallback? = null
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMap?.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSetLocationBinding.inflate(layoutInflater)

        // Google Map
        mMap = binding.mapView
        mMap?.onCreate(savedInstanceState)
        mMap?.getMapAsync(this)

        return binding.root
    }


    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(com.specindia.ecommerce.R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(com.specindia.ecommerce.R.string.ok)) { _, _ ->
            }
            .show()
    }

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

    // ================== GOOGLE MAP
    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(latitude,
            longitude)

        val marker: Marker? = googleMap.addMarker(MarkerOptions()
            .position(latLng)
//            .icon(getBitmapFromVector(requireActivity(), R.drawable.ic_location)))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
        googleMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap((activity as HomeActivity)))
        // Enable GPS marker in Map
        if (ActivityCompat.checkSelfPermission((activity as HomeActivity),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                (activity as HomeActivity),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null)

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
                    getAndSetAddressOnTextView(GeoPoint(latitude, longitude), marker)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

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

    override fun onResume() {
        super.onResume()
        mMap?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap?.onPause()
    }

    override fun onStart() {
        super.onStart()
        mMap?.onStart()

//        when {
//            PermissionUtils.checkAccessFineLocationGranted(requireActivity()) -> {
//                when {
//                    PermissionUtils.isLocationEnabled(requireActivity()) -> {
//                        setUpLocationListener()
//                    }
//                    else -> {
//                        PermissionUtils.showGPSNotEnabledDialog(requireActivity())
//                    }
//                }
//            }
//            else -> {
//                PermissionUtils.askAccessFineLocationPermission(
//                    (activity as HomeActivity),
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
//            }
//        }
    }

    override fun onStop() {
        super.onStop()
        mMap?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap?.onLowMemory()
    }


//    @SuppressLint("MissingPermission")
//    private fun setUpLocationListener() {
//        val fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient((activity as HomeActivity))
//        // for getting the current location update after every 2 seconds with high accuracy
//
//        fusedLocationProviderClient.requestLocationUpdates(
//            locationRequest,
//            object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    for (location in locationResult.locations) {
//                        Log.d("MY LOCATION", location.latitude.toString())
//                        Log.d("MY LOCATION", location.longitude.toString())
//                    }
//                    // Things don't end here
//                    // You may also update the location on your web app
//                }
//            },
//            Looper.myLooper()
//        )
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        // A random request code to listen on later
//        permissions: Array<out String>,
//        grantResults: IntArray,
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            // Location Permission
//            LOCATION_PERMISSION_REQUEST_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    when {
//                        PermissionUtils.isLocationEnabled(requireActivity()) -> {
//                            setUpLocationListener()
//                            // Setting things up
//                        }
//                        else -> {
//                            PermissionUtils.showGPSNotEnabledDialog(requireActivity())
//                        }
//                    }
//                } else {
//                    requireActivity().showShortToast("Permission not granted")
//                }
//            }
//        }
//    }
}