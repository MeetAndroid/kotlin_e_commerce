package com.specindia.ecommerce.api.network

import com.specindia.ecommerce.api.datasource.Session
import com.specindia.ecommerce.util.Constants
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Singleton


class TokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        return if (Session.userToken.userToken.isNotEmpty()) {
            val newRequest: Request = chain.request().newBuilder()
                .addHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .addHeader(
                    Constants.AUTHORIZATION,
                    Constants.BEARER + " " + Session.userToken.userToken
                )
                .build()
            chain.proceed(newRequest)
        } else {
            val newRequest: Request = chain.request().newBuilder()
                .build()
            chain.proceed(newRequest)
        }
    }
}