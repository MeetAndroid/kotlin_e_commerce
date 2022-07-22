package com.specindia.ecommerce.models.response.home


import com.google.gson.annotations.SerializedName

data class RestaurantDetailsResponse(
    @SerializedName("data")
    val data: RestaurantDetailsData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int
)