package com.specindia.ecommerce.api.datasource

import com.specindia.ecommerce.api.EcommerceApiService
import javax.inject.Inject

class EcommerceRemoteDataSource @Inject constructor(private val apiService: EcommerceApiService) {
    suspend fun doRegistration(headerMap: Map<String, String>, parameters: String) =
        apiService.doRegistration(headerMap, parameters)

    suspend fun doLogin(headerMap: Map<String, String>, parameters: String) =
        apiService.doLogin(headerMap, parameters)

    suspend fun doSocialLogin(headerMap: Map<String, String>, parameters: String) =
        apiService.doSocialSignUp(headerMap, parameters)

    suspend fun getDashboardList(headerMap: Map<String, String>) =
        apiService.getDashboardList(headerMap)

    suspend fun getMenuList(headerMap: Map<String, String>) =
        apiService.getMenuList(headerMap)

    suspend fun getRestaurantDetails(headerMap: Map<String, String>, id: Int) =
        apiService.getRestaurantDetails(headerMap, id)

    suspend fun getProductsByRestaurant(headerMap: Map<String, String>, parameters: String) =
        apiService.getProductsByRestaurant(headerMap, parameters)


    suspend fun getOrderDetails(headerMap: Map<String, String>, id: Int) =
        apiService.getOrderDetails(headerMap, id)

    suspend fun getAllProducts(headerMap: Map<String, String>, parameters: String) =
        apiService.getAllProducts(headerMap, parameters)

    suspend fun getAllRestaurant(headerMap: Map<String, String>, parameters: String) =
        apiService.getAllRestaurants(headerMap, parameters)

    suspend fun getSearch(headerMap: Map<String, String>, parameters: String) =
        apiService.getSearch(headerMap, parameters)

    suspend fun getCart(headerMap: Map<String, String>) =
        apiService.getCart(headerMap)

    suspend fun addUpdateToCart(headerMap: Map<String, String>, parameters: String) =
        apiService.addUpdateToCart(headerMap,parameters)

    suspend fun removeFromCart(headerMap: Map<String, String>, parameters: String) =
        apiService.removeFromCart(headerMap,parameters)

    suspend fun getOrderList(headerMap: Map<String, String>, parameters: String) =
        apiService.getOrderList(headerMap,parameters)

    suspend fun createOrder(headerMap: Map<String, String>, parameters: String) =
        apiService.createOrder(headerMap,parameters)

    suspend fun addOrUpdateAddress(headerMap: Map<String, String>, parameters: String) =
        apiService.addOrUpdateAddress(headerMap,parameters)

    suspend fun getAddressList(headerMap: Map<String, String>) =
        apiService.getAddressList(headerMap)

    suspend fun setPrimaryAddress(headerMap: Map<String, String>, parameters: String) =
        apiService.setPrimaryAddress(headerMap,parameters)


    suspend fun removeAddress(headerMap: Map<String, String>, parameters: String) =
        apiService.removeAddress(headerMap,parameters)

}

