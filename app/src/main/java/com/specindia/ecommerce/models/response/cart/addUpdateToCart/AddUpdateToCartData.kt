package com.specindia.ecommerce.models.response.cart.addUpdateToCart


import com.google.gson.annotations.SerializedName

data class AddUpdateToCartData(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)