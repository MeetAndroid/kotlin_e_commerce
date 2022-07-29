package com.specindia.ecommerce.api

import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.order.OrderDetailsResponse
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantResponse
import com.specindia.ecommerce.models.response.home.restaurantDetails.RestaurantDetailsResponse
import com.specindia.ecommerce.models.response.login.LoginResponse
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.models.response.social.SocialLoginResponse
import com.specindia.ecommerce.util.Constants.Companion.CUSTOMER_END_POINT
import com.specindia.ecommerce.util.Constants.Companion.DASH_BOARD_LIST
import com.specindia.ecommerce.util.Constants.Companion.GET_MENU_LIST
import com.specindia.ecommerce.util.Constants.Companion.GET_ORDER_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.GET_PRODUCT_BY_RESTRAUNT
import com.specindia.ecommerce.util.Constants.Companion.GET_RESTAURANT_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.LOGIN
import com.specindia.ecommerce.util.Constants.Companion.ORDER_END_POINT
import com.specindia.ecommerce.util.Constants.Companion.PRODUCT_END_POINT
import com.specindia.ecommerce.util.Constants.Companion.SIGN_UP
import com.specindia.ecommerce.util.Constants.Companion.SOCIAL_SIGN_UP
import retrofit2.Response
import retrofit2.http.*

interface EcommerceApiService {


    @POST("""$CUSTOMER_END_POINT$SIGN_UP""")
    suspend fun doRegistration(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<RegistrationResponse>

    @POST("""$CUSTOMER_END_POINT$LOGIN""")
    suspend fun doLogin(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<LoginResponse>

    @POST("""$CUSTOMER_END_POINT$SOCIAL_SIGN_UP""")
    suspend fun doSocialSignUp(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<SocialLoginResponse>

    @GET("""$CUSTOMER_END_POINT$DASH_BOARD_LIST""")
    suspend fun getDashboardList(
        @HeaderMap headers: Map<String, String>
    ): Response<DashboardListResponse>


    @GET("""$CUSTOMER_END_POINT$GET_MENU_LIST""")
    suspend fun getMenuList(
        @HeaderMap headers: Map<String, String>
    ): Response<MenuListResponse>


    @GET("""$CUSTOMER_END_POINT$GET_RESTAURANT_DETAILS""")
    suspend fun getRestaurantDetails(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int
    ): Response<RestaurantDetailsResponse>


    @POST("""$PRODUCT_END_POINT$GET_PRODUCT_BY_RESTRAUNT""")
    suspend fun getProductsByRestaurant(
        @HeaderMap headers: Map<String, String>,
        @Body parameters: String
    ): Response<ProductsByRestaurantResponse>

    @GET("""$ORDER_END_POINT$GET_ORDER_DETAILS""")
    suspend fun getOrderDetails(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int
    ): Response<OrderDetailsResponse>
}