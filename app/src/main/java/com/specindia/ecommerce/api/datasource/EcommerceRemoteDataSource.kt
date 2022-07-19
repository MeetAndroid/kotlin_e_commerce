package com.specindia.ecommerce.api.datasource

import com.specindia.ecommerce.api.EcommerceApiService
import javax.inject.Inject

class EcommerceRemoteDataSource @Inject constructor(private val apiService: EcommerceApiService) {
    suspend fun doRegistration(headerMap: Map<String, String>,parameters: String) =
        apiService.doRegistration(headerMap,parameters)

    suspend fun doLogin(headerMap: Map<String, String>,parameters: String) =
        apiService.doLogin(headerMap,parameters)

    suspend fun doSocialLogin(headerMap: Map<String, String>,parameters: String) =
        apiService.doSocialSignUp(headerMap,parameters)

    suspend fun getDashboardList(headerMap: Map<String, String>) =
        apiService.getDashboardList(headerMap)
}