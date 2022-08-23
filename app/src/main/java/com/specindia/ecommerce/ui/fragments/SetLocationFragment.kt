package com.specindia.ecommerce.ui.fragments

import android.content.Context
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
import androidx.appcompat.app.AlertDialog
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
import com.specindia.ecommerce.util.showProgressDialog
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
    private lateinit var customProgressDialog: AlertDialog

    // Google Map
    private var mMap: MapView? = null
    private var latitude = 21.8380
    private var longitude = 73.7191

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
        setUpProgressDialog()
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
                val fullAddress = binding.tvAddress.text.toString().trim()
                it.findNavController()
                    .navigate(SetLocationFragmentDirections.actionSetLocationFragmentToAddAddressFragment(
                        fullAddress = fullAddress,
                        latitude = latitude.toString(),
                        longitude = longitude.toString()))
            }
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
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
        // Enable GPS marker in Map
        //googleMap.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17f), 1000, null)

        googleMap.setOnCameraMoveListener {
            val midLatLng = googleMap.cameraPosition.target
            if (marker != null) {
                marker.position = midLatLng
                val nowLocation = marker.position
                Log.d("nowLocation", nowLocation.toString())

                latitude = nowLocation.latitude
                longitude = nowLocation.longitude

                try {
                    getAndSetAddressOnTextView(GeoPoint(latitude, longitude))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun getAndSetAddressOnTextView(location: GeoPoint) {
        var addressList: ArrayList<Address> = ArrayList()
        var fullAddress = ""
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
                fullAddress = addressList[0].getAddressLine(0)
                binding.tvAddress.text = fullAddress
                Log.d("fullAddress", fullAddress)
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

}