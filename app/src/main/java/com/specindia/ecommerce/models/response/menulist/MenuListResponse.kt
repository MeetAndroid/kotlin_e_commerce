package com.specindia.ecommerce.models.response.menulist


import com.google.gson.annotations.SerializedName

data class MenuListResponse(
    @SerializedName("data")
    val data: MenuListData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val statusCode: Int
)