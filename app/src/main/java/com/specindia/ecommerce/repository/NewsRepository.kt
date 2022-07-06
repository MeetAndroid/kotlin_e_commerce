package com.specindia.ecommerce.repository

import com.specindia.ecommerce.api.RetrofitInstance
import com.specindia.ecommerce.db.ArticleDatabase
import com.specindia.ecommerce.models.Article

class NewsRepository(private val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    // DB operation

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}