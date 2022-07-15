package com.specindia.ecommerce.models.response.registration


import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("data")
    val data: RegistrationData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int
)