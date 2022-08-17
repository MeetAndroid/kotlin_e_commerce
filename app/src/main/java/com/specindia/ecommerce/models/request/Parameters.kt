package com.specindia.ecommerce.models.request

data class Parameters(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val number: String = "",
    val password: String = "",
    val socialId: String = "",
    val socialType: Int = 0,
    val restaurantId: Int = 0,
    val productId: String = "",
    val id: String = "",
    val quantity: String = "",
    val amount: String = "",
    val pageNo: Int = 0,
    val limit: Int = 0,
    val text: String = "",
    val addressId: String = "",
    val extraCharges: String = "",
    val subtotal: String = "",
    val total: String = "",
    )