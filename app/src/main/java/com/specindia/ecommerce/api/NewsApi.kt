package com.specindia.ecommerce.api

import com.specindia.ecommerce.models.NewsResponse
import com.specindia.ecommerce.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


// https://newsapi.org/v2/top-headlines?country=us&apiKey=8be6b5fd90574ef9bf33c38f65309b6c&page=1

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}