package com.specindia.ecommerce.models.response.home


import com.google.gson.annotations.SerializedName

data class DashboardListData(
    @SerializedName("categoryList")
    val categoryList: List<Category>,
    @SerializedName("favoritesRestaurants")
    val favoritesRestaurants: List<Any>,
    @SerializedName("popularRestaurents")
    val popularRestaurents: List<PopularRestaurent>,
    @SerializedName("topProduct")
    val topProduct: List<TopProduct>
)