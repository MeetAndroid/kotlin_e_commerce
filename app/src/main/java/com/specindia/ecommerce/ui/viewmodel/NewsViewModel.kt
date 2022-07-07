package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.R
import com.specindia.ecommerce.models.NewsResponse
import com.specindia.ecommerce.repository.NewsRepository
import com.specindia.ecommerce.EcommerceApp
import com.specindia.ecommerce.util.Resource
import com.specindia.ecommerce.util.isConnected
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val app: EcommerceApp,
    private val newsRepository: NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    init {
        viewModelScope.launch {
            getBreakingNews("us")
            delay(5000)
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())

        if (app.isConnected) {
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        } else {
            breakingNews.postValue(Resource.Error(app.getString(R.string.message_no_internet_connection)))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}