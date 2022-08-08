package com.specindia.ecommerce.models.response.cart.getcart


import com.google.gson.annotations.SerializedName

data class GetCartData(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("orderId")
    val orderId: Any,
    @SerializedName("product")
    val product: Product,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int,
)