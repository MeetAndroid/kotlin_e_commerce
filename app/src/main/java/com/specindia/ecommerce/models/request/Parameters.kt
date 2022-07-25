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
    val pageNo: Int = 0,
    val limit: Int = 0
)