package com.specindia.ecommerce.api

import com.specindia.ecommerce.api.network.TokenInterceptor
import com.specindia.ecommerce.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


sealed class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            val interceptor = TokenInterceptor()

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(interceptor)
                .build()
            Retrofit.Builder()
                .baseUrl(Constants.NEWS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api: NewsApi by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}