package com.specindia.ecommerce.models.response.registration


import com.google.gson.annotations.SerializedName

data class RegistrationData(
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
    val number: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)