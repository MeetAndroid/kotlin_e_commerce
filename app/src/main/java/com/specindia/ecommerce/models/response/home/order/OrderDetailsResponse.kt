package com.specindia.ecommerce.models.response.home.order

import com.google.gson.annotations.SerializedName

data class OrderDetailsResponse(

    @field:SerializedName("status_code")
    val statusCode: Int? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class CartsItem(

    @field:SerializedName("amount")
    val amount: String? = null,

    @field:SerializedName("product")
    val product: Product? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("productId")
    val productId: Int? = null,

    @field:SerializedName("orderId")
    val orderId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class Data(

    @field:SerializedName("transcationType")
    val transcationType: Any? = null,

    @field:SerializedName("orderStatus")
    val orderStatus: Any? = null,

    @field:SerializedName("deliveryuserId")
    val deliveryuserId: Any? = null,

    @field:SerializedName("couponId")
    val couponId: Any? = null,

    @field:SerializedName("restaurantId")
    val restaurantId: Int? = null,

    @field:SerializedName("extraCharges")
    val extraCharges: String? = null,

    @field:SerializedName("userId")
    val userId: Int? = null,

    @field:SerializedName("transactionId")
    val transactionId: Any? = null,

    @field:SerializedName("addressId")
    val addressId: Int? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("carts")
    val carts: ArrayList<CartsItem> = ArrayList(),

    @field:SerializedName("subtotal")
    val subtotal: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("deliveryDate")
    val deliveryDate: Any? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class Product(

    @field:SerializedName("productImage")
    val productImage: String? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("RestaurantId")
    val restaurantId: Int? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("categoryName")
    val categoryName: String? = null,

    @field:SerializedName("productName")
    val productName: String? = null,

    @field:SerializedName("categoryId")
    val categoryId: Int? = null
)
