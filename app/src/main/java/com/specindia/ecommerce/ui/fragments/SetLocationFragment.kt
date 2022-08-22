package com.specindia.ecommerce.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class SetLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentSetLocationBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    // Google Map
    private var mMap: MapView? = null
    private val latitude = 23.036957296395084
    private val longitude = 72.56168172566267

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
                        fullAddress = fullAddress))
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

    // ================== GOOGLE MAP
    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(latitude,
            longitude)

        val marker: Marker? = googleMap.addMarker(MarkerOptions()
            .position(latLng)
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
                binding.tvAddress.text =
                    getFullAddress(GeoPoint(nowLocation.latitude, nowLocation.longitude))
            }
        }
    }

    private fun getFullAddress(location: GeoPoint): String? {
        var addressList: ArrayList<Address> = ArrayList()
        var fullAddress = ""
        val geocoder = Geocoder((activity as HomeActivity), Locale.getDefault())
        try {

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

            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (e: IOException) {
            e.printStackTrace()
        }
        fullAddress = addressList[0].getAddressLine(0)
        return fullAddress
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