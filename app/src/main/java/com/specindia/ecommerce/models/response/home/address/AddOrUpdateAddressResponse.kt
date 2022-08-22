package com.specindia.ecommerce.models.response.home.address


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class AddOrUpdateAddressResponse(
    @SerializedName("data")
    val data: AddOrUpdateAddressData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()