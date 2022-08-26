package com.specindia.ecommerce.models.response.home.common

import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class CommonResponse(
    @SerializedName("data")
    val data: Any, // This can be ArrayList or primitive type

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()