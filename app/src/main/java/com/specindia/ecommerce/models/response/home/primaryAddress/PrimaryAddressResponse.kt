package com.specindia.ecommerce.models.response.home.primaryAddress

import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class PrimaryAddressResponse(
    @SerializedName("data")
    val data: ArrayList<Int>,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()