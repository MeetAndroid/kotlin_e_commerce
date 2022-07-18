package com.specindia.ecommerce.api

import com.specindia.ecommerce.models.response.login.LoginResponse
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.models.response.social.SocialLoginResponse
import com.specindia.ecommerce.util.Constants.Companion.CONTENT_TYPE_JSON
import com.specindia.ecommerce.util.Constants.Companion.LOGIN
import com.specindia.ecommerce.util.Constants.Companion.SIGN_UP
import com.specindia.ecommerce.util.Constants.Companion.SOCIAL_SIGN_UP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EcommerceApiService {

    @Headers(CONTENT_TYPE_JSON)
    @POST(SIGN_UP)
    suspend fun doRegistration(@Body parameters: String): Response<RegistrationResponse>

    @Headers(CONTENT_TYPE_JSON)
    @POST(LOGIN)
    suspend fun doLogin(@Body parameters: String): Response<LoginResponse>

    @Headers(CONTENT_TYPE_JSON)
    @POST(SOCIAL_SIGN_UP)
    suspend fun doSocialSignUp(@Body parameters: String): Response<SocialLoginResponse>
}