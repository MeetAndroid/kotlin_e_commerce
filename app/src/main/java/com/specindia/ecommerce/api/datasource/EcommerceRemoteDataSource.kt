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

    suspend fun getMenuList(headerMap: Map<String, String>) =
        apiService.getMenuList(headerMap)

    suspend fun getRestaurantDetails(headerMap: Map<String, String>,id:Int) =
        apiService.getRestaurantDetails(headerMap,id)

    suspend fun getProductsByRestaurant(headerMap: Map<String, String>,parameters: String) =
        apiService.getProductsByRestaurant(headerMap,parameters)


    suspend fun getOrderDetails(headerMap: Map<String, String>,id:Int) =
        apiService.getOrderDetails(headerMap,id)
}