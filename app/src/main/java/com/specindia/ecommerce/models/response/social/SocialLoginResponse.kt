package com.specindia.ecommerce.models.response.social


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.models.response.AuthResponseData

data class SocialLoginResponse(
    @SerializedName("data")
    val data: AuthResponseData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int
)