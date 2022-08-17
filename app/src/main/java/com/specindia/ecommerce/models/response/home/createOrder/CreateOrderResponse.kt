package com.specindia.ecommerce.models.response.home.createOrder


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class CreateOrderResponse(
    @SerializedName("data")
    val data: CreateOrderData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()