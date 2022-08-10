package com.specindia.ecommerce.models.response.home.orderlist

import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class OrderListResponse(
    @SerializedName("data")
    val data: ArrayList<OrderData> = ArrayList(),
    override var message: String,
    override var status: String,
    override var status_code: Int,
) : ApiResponse()

data class OrderData(

    @SerializedName("transcationType")
    val transcationType: Any? = null,

    @SerializedName("orderStatus")
    val orderStatus: Any? = null,

    @SerializedName("deliveryuserId")
    val deliveryuserId: Any? = null,

    @SerializedName("couponId")
    val couponId: Any? = null,

    @SerializedName("restaurantId")
    val restaurantId: Int? = null,

    @SerializedName("extraCharges")
    val extraCharges: String? = null,

    @SerializedName("userId")
    val userId: Int? = null,

    @SerializedName("transactionId")
    val transactionId: Any? = null,

    @SerializedName("addressId")
    val addressId: Int? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("total")
    val total: String? = null,

    @SerializedName("subtotal")
    val subtotal: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("deliveryDate")
    val deliveryDate: Any? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,
)
