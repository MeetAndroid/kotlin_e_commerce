package com.specindia.ecommerce.models.response

import com.google.gson.annotations.SerializedName

data class AuthResponseData(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("number")
    val number: Long,
    @SerializedName("password")
    val password: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("role")
    val role: Any,
    @SerializedName("socialId")
    val socialId: Any,
    @SerializedName("socialType")
    val socialType: Any,
    @SerializedName("token")
    val token: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
)