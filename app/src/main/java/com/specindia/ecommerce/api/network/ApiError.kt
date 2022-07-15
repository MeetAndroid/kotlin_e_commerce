package com.specindia.ecommerce.api.network


import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("error")
    val error: String? = null
)