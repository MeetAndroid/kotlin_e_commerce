package com.specindia.ecommerce.models.response.home.productsbyrestaurant

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

data class ProductsByRestaurantData(

    @SerializedName("id")
    val productId: Int,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("productImage")
    val productImage: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("RestaurantId")
    val restaurantId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @Ignore
    var totalQty: Int = 0,
    @Ignore
    var cartId: Int = 0,
    @Ignore
    var isCartExist: Boolean = false
)



