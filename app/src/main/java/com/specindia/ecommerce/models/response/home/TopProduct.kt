package com.specindia.ecommerce.models.response.home


import com.google.gson.annotations.SerializedName

data class TopProduct(
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: Int,
    @SerializedName("productImage")
    val productImage: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("RestaurantId")
    val restaurantId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)