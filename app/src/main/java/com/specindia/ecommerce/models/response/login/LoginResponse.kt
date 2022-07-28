package com.specindia.ecommerce.models.response.login


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse
import com.specindia.ecommerce.models.response.AuthResponseData

data class LoginResponse(
    @SerializedName("data")
    val data: AuthResponseData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()