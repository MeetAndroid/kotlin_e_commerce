package com.specindia.ecommerce.api.network

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response


abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            val errorResponse: ApiError =
                Gson().fromJson(response.errorBody()?.charStream(), ApiError::class.java)

            Log.e("BASE_API_ERROR", errorResponse.toString())

            return if (errorResponse.message != null) {
                error(errorResponse.message)
            } else {
                error(errorResponse.error.toString())
            }
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)
}