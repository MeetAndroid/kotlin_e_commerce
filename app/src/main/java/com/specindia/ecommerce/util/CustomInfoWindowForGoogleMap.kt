package com.specindia.ecommerce.util

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.textview.MaterialTextView
import com.specindia.ecommerce.R

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mWindow: View =
        (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun infoWindowText(marker: Marker, view: View) {

        val tvAddress = view.findViewById<MaterialTextView>(R.id.tvAddressInfo)
        tvAddress.text = marker.title
    }

    override fun getInfoContents(marker: Marker): View {
        infoWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        infoWindowText(marker, mWindow)
        return mWindow
    }
}