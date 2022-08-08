package com.specindia.ecommerce.models.response.cart.removeFromCart


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class RemoveFromCartResponse(
    @SerializedName("data")
    val data: Int,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()