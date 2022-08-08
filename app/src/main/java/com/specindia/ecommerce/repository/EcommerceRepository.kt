package com.specindia.ecommerce.repository

import com.specindia.ecommerce.api.datasource.EcommerceRemoteDataSource
import com.specindia.ecommerce.api.network.BaseApiResponse
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.cart.addUpdateToCart.AddUpdateToCartResponse
import com.specindia.ecommerce.models.response.cart.getcart.GetCartResponse
import com.specindia.ecommerce.models.response.cart.removeFromCart.RemoveFromCartResponse
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.SearchResponse
import com.specindia.ecommerce.models.response.home.order.OrderDetailsResponse
import com.specindia.ecommerce.models.response.home.product.AllRestaurant
import com.specindia.ecommerce.models.response.home.product.ViewAllData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantResponse
import com.specindia.ecommerce.models.response.home.restaurantDetails.RestaurantDetailsResponse
import com.specindia.ecommerce.models.response.login.LoginResponse
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.models.response.social.SocialLoginResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class EcommerceRepository @Inject constructor(
    private val remoteDataSource: EcommerceRemoteDataSource
) : BaseApiResponse() {
    suspend fun doRegistration(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<RegistrationResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.doRegistration(headerMap, parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun doLogin(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<LoginResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.doLogin(headerMap, parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun doSocialLogin(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<SocialLoginResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.doSocialLogin(headerMap, parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDashboardList(
        headerMap: Map<String, String>
    ): Flow<NetworkResult<DashboardListResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getDashboardList(headerMap) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMenuList(
        headerMap: Map<String, String>
    ): Flow<NetworkResult<MenuListResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getMenuList(headerMap) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getRestaurantDetails(
        headerMap: Map<String, String>,
        id:Int
    ): Flow<NetworkResult<RestaurantDetailsResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getRestaurantDetails(headerMap,id) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProductsByRestaurant(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<ProductsByRestaurantResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getProductsByRestaurant(headerMap,parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getOrderDetails(
        headerMap: Map<String, String>,
        id:Int
    ): Flow<NetworkResult<OrderDetailsResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getOrderDetails(headerMap,id) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllProducts(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<ViewAllData>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getAllProducts(headerMap,parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllRestaurants(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<AllRestaurant>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getAllRestaurant(headerMap,parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getSearch(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<SearchResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getSearch(headerMap,parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCart(
        headerMap: Map<String, String>
    ): Flow<NetworkResult<GetCartResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCart(headerMap) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addUpdateToCart(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<AddUpdateToCartResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.addUpdateToCart(headerMap,parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun removeFromCart(
        headerMap: Map<String, String>,
        parameters: String
    ): Flow<NetworkResult<RemoveFromCartResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.removeFromCart(headerMap,parameters) })
        }.flowOn(Dispatchers.IO)
    }
}