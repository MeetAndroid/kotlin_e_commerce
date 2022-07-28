package com.specindia.ecommerce.models.response.social


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse
import com.specindia.ecommerce.models.response.AuthResponseData

data class SocialLoginResponse(
    @SerializedName("data")
    val data: AuthResponseData,

    override var message: String,
    override var status: String,
    override var status_code: Int
): ApiResponse()