package com.specindia.ecommerce.models.response.home.order


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class OrderDetailsResponse(
    @SerializedName("data")
    val data: OrderDetailsData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()