package com.specindia.ecommerce.repository

import com.specindia.ecommerce.api.datasource.EcommerceRemoteDataSource
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.api.network.BaseApiResponse
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.login.LoginResponse
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
    suspend fun doRegistration(parameters: String): Flow<NetworkResult<RegistrationResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.doRegistration(parameters) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun doLogin(parameters: String): Flow<NetworkResult<LoginResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.doLogin(parameters) })
        }.flowOn(Dispatchers.IO)
    }
}