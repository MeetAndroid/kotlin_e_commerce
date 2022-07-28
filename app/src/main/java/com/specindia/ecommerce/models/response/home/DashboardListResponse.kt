package com.specindia.ecommerce.models.response.home


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class DashboardListResponse(
    @SerializedName("data")
    val data: DashboardListData,

    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()