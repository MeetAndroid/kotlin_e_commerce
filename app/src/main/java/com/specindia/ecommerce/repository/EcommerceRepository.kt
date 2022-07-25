package com.specindia.ecommerce.repository

import com.specindia.ecommerce.api.datasource.EcommerceRemoteDataSource
import com.specindia.ecommerce.api.network.BaseApiResponse
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.RestaurantDetailsResponse
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
}