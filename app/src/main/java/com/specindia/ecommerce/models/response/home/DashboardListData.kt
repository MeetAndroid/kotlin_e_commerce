package com.specindia.ecommerce.models.response.home


import com.google.gson.annotations.SerializedName

data class DashboardListData(
    @SerializedName("categoryList")
    val categoryList: MutableList<Category>,
    @SerializedName("favoritesRestaurants")
    val favoritesRestaurants: MutableList<Any>,
    @SerializedName("popularRestaurents")
    val popularRestaurents: MutableList<PopularRestaurent>,
    @SerializedName("topProduct")
    val topProduct: MutableList<TopProduct>
)