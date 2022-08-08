package com.specindia.ecommerce.models.response.home

import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class SearchResponse(
    @SerializedName("data")
    val data: ArrayList<SearchItem>,
    override var message: String,
    override var status: String,
    override var status_code: Int
) : ApiResponse()

data class SearchItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("categoryId")
    val categoryId: Int,

    @SerializedName("categoryName")
    val categoryName: String,

    @SerializedName("productImage")
    val productImage: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("RestaurantId")
    val RestaurantId: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)
