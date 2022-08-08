package com.specindia.ecommerce.models.response.cart.addUpdateToCart


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class AddUpdateToCartResponse(
    @SerializedName("data")
    val data: AddUpdateToCartData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()