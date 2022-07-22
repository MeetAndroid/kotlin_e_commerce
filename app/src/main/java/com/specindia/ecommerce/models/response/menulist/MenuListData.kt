package com.specindia.ecommerce.models.response.menulist


import com.google.gson.annotations.SerializedName

data class MenuListData(
    @SerializedName("menu")
    val menu: MutableList<Menu>
)