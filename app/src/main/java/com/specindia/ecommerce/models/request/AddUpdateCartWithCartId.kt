package com.specindia.ecommerce.models.request

data class AddUpdateCartWithCartId(
    val id: String = "",
    val quantity: String = "",
    val amount: String = ""
)