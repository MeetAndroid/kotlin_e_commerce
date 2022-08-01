package com.specindia.ecommerce.models.response.home.product

import com.google.gson.annotations.SerializedName

data class AllRestaurant(

    @SerializedName("status_code")
    val statusCode: Int? = null,

    @SerializedName("data")
    val data: ArrayList<RestaurantItems>? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String? = null
)

data class RestaurantItems(

    @SerializedName("latlang")
    val latlang: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)
