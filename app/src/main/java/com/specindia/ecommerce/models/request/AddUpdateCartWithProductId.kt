package com.specindia.ecommerce.models.request

data class AddUpdateCartWithProductId(
    val productId: String = "",
    val quantity: String = "",
    val amount: String = ""
)