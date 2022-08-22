package com.specindia.ecommerce.models.response.home.getaddress


import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class GetAddressListResponse(
    @SerializedName("data")
    val data: ArrayList<GetAddressListData>,
    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()