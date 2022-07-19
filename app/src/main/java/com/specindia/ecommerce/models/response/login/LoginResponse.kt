package com.specindia.ecommerce.models.response.login


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.models.response.AuthResponseData

data class LoginResponse(
    @SerializedName("data")
    val data: AuthResponseData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int
)