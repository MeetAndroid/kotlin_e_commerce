package com.specindia.ecommerce.models.response.menulist


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String
)