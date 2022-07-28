package com.specindia.ecommerce.models.response.home.restaurantDetails


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class RestaurantDetailsResponse(
    @SerializedName("data")
    val data: RestaurantDetailsData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()