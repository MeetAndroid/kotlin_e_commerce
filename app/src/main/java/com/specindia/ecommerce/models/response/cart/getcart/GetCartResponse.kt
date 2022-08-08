package com.specindia.ecommerce.models.response.cart.getcart


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class GetCartResponse(
    @SerializedName("data")
    val data: ArrayList<GetCartData>,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()