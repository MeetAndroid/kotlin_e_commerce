package com.specindia.ecommerce.models.response.home.createOrder


import com.google.gson.annotations.SerializedName

data class CreateOrderData(
    @SerializedName("addressId")
    val addressId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("extraCharges")
    val extraCharges: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("restaurantId")
    val restaurantId: String,
    @SerializedName("subtotal")
    val subtotal: String,
    @SerializedName("total")
    val total: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)