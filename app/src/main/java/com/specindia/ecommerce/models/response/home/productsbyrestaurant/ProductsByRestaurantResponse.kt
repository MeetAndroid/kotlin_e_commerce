package com.specindia.ecommerce.models.response.home.productsbyrestaurant


import com.google.gson.annotations.SerializedName

data class ProductsByRestaurantResponse(
    @SerializedName("data")
    val data: ArrayList<ProductsByRestaurantData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int
)