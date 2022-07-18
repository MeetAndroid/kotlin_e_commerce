package com.specindia.ecommerce.models.response.social


import com.google.gson.annotations.SerializedName

data class SocialLoginData(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("socialId")
    val socialId: String,
    @SerializedName("socialType")
    val socialType: Int,
    @SerializedName("token")
    val token: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)