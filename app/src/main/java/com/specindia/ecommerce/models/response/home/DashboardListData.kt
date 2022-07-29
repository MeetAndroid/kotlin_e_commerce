package com.specindia.ecommerce.models.response.home


import com.google.gson.annotations.SerializedName

data class DashboardListData(
    @SerializedName("categoryList")
    val categoryList: ArrayList<Category>,
    @SerializedName("favoritesRestaurants")
    val favoritesRestaurants: ArrayList<Any>,
    @SerializedName("popularRestaurents")
    val popularRestaurents: ArrayList<PopularRestaurent>,
    @SerializedName("topProduct")
    val topProduct: ArrayList<TopProduct>
)