package com.specindia.ecommerce.api

import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.login.LoginResponse
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.models.response.social.SocialLoginResponse
import com.specindia.ecommerce.util.Constants.Companion.CONTENT_TYPE_JSON
import com.specindia.ecommerce.util.Constants.Companion.DASH_BOARD_LIST
import com.specindia.ecommerce.util.Constants.Companion.GET_MENU_LIST
import com.specindia.ecommerce.util.Constants.Companion.LOGIN
import com.specindia.ecommerce.util.Constants.Companion.SIGN_UP
import com.specindia.ecommerce.util.Constants.Companion.SOCIAL_SIGN_UP
import retrofit2.Response
import retrofit2.http.*

interface EcommerceApiService {


    @POST(SIGN_UP)
    suspend fun doRegistration(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<RegistrationResponse>

    @POST(LOGIN)
    suspend fun doLogin(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<LoginResponse>


    @POST(SOCIAL_SIGN_UP)
    suspend fun doSocialSignUp(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<SocialLoginResponse>

    @GET(DASH_BOARD_LIST)
    suspend fun getDashboardList(
        @HeaderMap headers: Map<String, String>
    ): Response<DashboardListResponse>

    @GET(GET_MENU_LIST)
    suspend fun getMenuList(
        @HeaderMap headers: Map<String, String>
    ): Response<MenuListResponse>

}