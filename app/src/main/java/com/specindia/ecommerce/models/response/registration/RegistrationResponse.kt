package com.specindia.ecommerce.models.response.registration


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse
import com.specindia.ecommerce.models.response.AuthResponseData

data class RegistrationResponse(
    @SerializedName("data")
    val data: AuthResponseData,
    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()