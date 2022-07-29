package com.specindia.ecommerce.models.response.home.order


import com.google.gson.annotations.SerializedName

data class OrderDetailsData(
    @SerializedName("addressId")
    val addressId: Int,
    @SerializedName("carts")
    val carts: MutableList<Any>,
    @SerializedName("couponId")
    val couponId: Any,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("deliveryDate")
    val deliveryDate: Any,
    @SerializedName("deliveryuserId")
    val deliveryuserId: Any,
    @SerializedName("extraCharges")
    val extraCharges: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("orderStatus")
    val orderStatus: Any,
    @SerializedName("restaurantId")
    val restaurantId: String,
    @SerializedName("subtotal")
    val subtotal: String,
    @SerializedName("total")
    val total: String,
    @SerializedName("transactionId")
    val transactionId: Any,
    @SerializedName("transcationType")
    val transcationType: Any,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)