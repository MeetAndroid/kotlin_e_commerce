package com.specindia.ecommerce.api.network


abstract class ApiResponse {
    abstract var message: String
    abstract var status: String
    abstract var status_code: Int
}