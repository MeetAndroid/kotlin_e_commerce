package com.specindia.ecommerce.models.response.home.address


import com.google.gson.annotations.SerializedName

data class AddOrUpdateAddressData(
    @SerializedName("addressType")
    val addressType: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("firstLine")
    val firstLine: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lang")
    val lang: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("primary")
    val primary: Boolean,
    @SerializedName("secondLine")
    val secondLine: String,
    @SerializedName("thirdLine")
    val thirdLine: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)