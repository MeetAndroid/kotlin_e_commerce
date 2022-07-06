package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.specindia.ecommerce.repository.NewsRepository
import com.specindia.ecommerce.ui.EcommerceApp

class NewsViewModelProviderFactory(
    private val app: EcommerceApp,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(app, newsRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}