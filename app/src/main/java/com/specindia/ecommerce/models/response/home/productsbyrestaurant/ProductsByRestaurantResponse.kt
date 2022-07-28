package com.specindia.ecommerce.models.response.home.productsbyrestaurant


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class ProductsByRestaurantResponse(
    @SerializedName("data")
    val data: ArrayList<ProductsByRestaurantData>,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()