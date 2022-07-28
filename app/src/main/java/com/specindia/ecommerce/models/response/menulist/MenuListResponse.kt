package com.specindia.ecommerce.models.response.menulist


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class MenuListResponse(
    @SerializedName("data")
    val data: MenuListData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()