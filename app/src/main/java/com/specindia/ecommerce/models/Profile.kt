package com.specindia.ecommerce.models

import android.graphics.drawable.Drawable

data class Profile(
    val image: Drawable?,
    val name: String,
    val content: String
)