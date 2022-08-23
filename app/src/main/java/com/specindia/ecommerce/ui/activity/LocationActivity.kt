package com.specindia.ecommerce.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.specindia.ecommerce.databinding.ActivityLocationBinding
import com.specindia.ecommerce.util.Constants.Companion.GPS_REQUEST
import com.specindia.ecommerce.util.Constants.Companion.LOCATION_REQUEST
import com.specindia.ecommerce.util.GpsUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var stringBuilder: StringBuilder? = null

    private var isContinue = false
    private var isGPS = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = (10 * 1000).toLong() // 10 seconds

        locationRequest!!.fastestInterval = (5 * 1000).toLong() // 5 seconds


        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        if (!isContinue) {
                            binding.txtLocation.text = String.format(Locale.US,
                                "%s - %s",
                                wayLatitude,
                                wayLongitude)
                        } else {
                            stringBuilder!!.append(wayLatitude)
                            stringBuilder!!.append("-")
                            stringBuilder!!.append(wayLongitude)
                            stringBuilder!!.append("\n\n")
                            binding.txtContinueLocation.text = stringBuilder.toString()
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient!!.removeLocationUpdates(locationCallback!!)
                        }
                    }
                }
            }
        }

        binding.btnLocation.setOnClickListener {
            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isContinue = false
            getLocation()
        }

        binding.btnContinueLocation.setOnClickListener { v: View? ->
            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isContinue = true
            stringBuilder = java.lang.StringBuilder()
            getLocation()
        }
    }


    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(this@LocationActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this@LocationActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@LocationActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST)
        } else {
            if (isContinue) {
                mFusedLocationClient!!.requestLocationUpdates(locationRequest!!,
                    locationCallback!!, null)
            } else {
                mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@LocationActivity) { location ->
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        binding.txtLocation.text = String.format(Locale.US,
                            "%s - %s",
                            wayLatitude,
                            wayLongitude)
                    } else {
                        mFusedLocationClient!!.requestLocationUpdates(locationRequest!!,
                            locationCallback!!, Looper.myLooper())
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (isContinue) {
                        mFusedLocationClient!!.requestLocationUpdates(locationRequest!!,
                            locationCallback!!, null)
                    } else {
                        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@LocationActivity) { location ->
                            if (location != null) {
                                wayLatitude = location.latitude
                                wayLongitude = location.longitude
                                binding.txtLocation.text = String.format(Locale.US,
                                    "%s - %s",
                                    wayLatitude,
                                    wayLongitude)
                            } else {
                                mFusedLocationClient!!.requestLocationUpdates(locationRequest!!,
                                    locationCallback!!, null)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true // flag maintain before get location
                Log.d("RESULT_OK", "GPS ON")
            }
        }
    }


}
