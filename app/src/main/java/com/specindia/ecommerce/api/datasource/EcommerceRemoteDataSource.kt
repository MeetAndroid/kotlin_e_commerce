package com.specindia.ecommerce.api.datasource

import com.specindia.ecommerce.api.EcommerceApiService
import javax.inject.Inject

class EcommerceRemoteDataSource @Inject constructor(private val apiService: EcommerceApiService) {
    suspend fun doRegistration(parameters: String) =
        apiService.doRegistration(parameters)

    suspend fun doLogin(parameters: String) =
        apiService.doLogin(parameters)
}