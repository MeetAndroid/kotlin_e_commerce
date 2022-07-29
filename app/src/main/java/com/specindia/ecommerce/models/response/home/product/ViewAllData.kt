package com.specindia.ecommerce.models.response.home.product

import com.google.gson.annotations.SerializedName
import com.specindia.ecommerce.api.network.ApiResponse

data class ViewAllData(
    @SerializedName("data")
    val data: ArrayList<ViewAllItems>? = null,
    override var message: String, override var status: String, override var status_code: Int,
) : ApiResponse()

data class ViewAllItems(

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("productImage")
    val productImage: String? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("RestaurantId")
    val restaurantId: Int? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("categoryName")
    val categoryName: String? = null,

    @SerializedName("productName")
    val productName: String? = null,

    @SerializedName("categoryId")
    val categoryId: Int? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)
