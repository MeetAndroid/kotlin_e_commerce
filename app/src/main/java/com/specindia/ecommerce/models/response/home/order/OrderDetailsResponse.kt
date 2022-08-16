package com.specindia.ecommerce.models.response.home.order

import com.google.gson.annotations.SerializedName

data class OrderDetailsResponse(

    @SerializedName("status_code")
    val statusCode: Int? = null,

    @SerializedName("data")
    val data: Data? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String? = null
)

data class CartsItem(

    @SerializedName("amount")
    val amount: String? = null,

    @SerializedName("product")
    val product: Product? = null,

    @SerializedName("quantity")
    val quantity: String? = null,

    @SerializedName("productId")
    val productId: Int? = null,

    @SerializedName("orderId")
    val orderId: Int? = null,

    @SerializedName("id")
    val id: Int? = null
)

data class Data(

    @SerializedName("transcationType")
    val transcationType: Any? = null,

    @SerializedName("orderStatus")
    val orderStatus: Any? = null,

    @SerializedName("deliveryuserId")
    val deliveryuserId: Any? = null,

    @SerializedName("couponId")
    val couponId: Any? = null,

    @SerializedName("restaurantId")
    val restaurantId: Int? = null,

    @SerializedName("extraCharges")
    val extraCharges: String? = null,

    @SerializedName("userId")
    val userId: Int? = null,

    @SerializedName("transactionId")
    val transactionId: Any? = null,

    @SerializedName("addressId")
    val addressId: Int? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("total")
    val total: String? = null,

    @SerializedName("carts")
    val carts: ArrayList<CartsItem> = ArrayList(),

    @SerializedName("subtotal")
    val subtotal: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("deliveryDate")
    val deliveryDate: Any? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class Product(

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
    val categoryId: Int? = null
)
