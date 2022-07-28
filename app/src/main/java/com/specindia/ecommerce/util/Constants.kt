package com.specindia.ecommerce.util

import androidx.datastore.preferences.core.stringPreferencesKey

class Constants {
    companion object {
        const val NEWS_API_KEY = "8be6b5fd90574ef9bf33c38f65309b6c"
        const val NEWS_BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20

        // =================== Ecommerce App Constants

        // Splash Time Out
        const val SPLASH_SCREEN_TIME_OUT = 3000L
        const val READ_TIME_OUT = 120L
        const val WRITE_TIME_OUT = 120L
        const val CONNECTION_TIME_OUT = 120L

        // Headers
        const val CONTENT_TYPE_JSON = "Content-Type: application/json"
        const val ACCEPT = "accept"
        const val CONTENT_TYPE = "Content-Type"
        const val APPLICATION_JSON = "application/json"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"

        // End Points
        const val CUSTOMER_END_POINT = "customer/"
        const val PRODUCT_END_POINT = "product/"
        const val CART_END_POINT = "cart/"
        const val ADDRESS_END_POINT = "address/"
        const val ORDER_END_POINT = "order/"

        // Methods
        const val SIGN_UP = "signUp"
        const val LOGIN = "login"
        const val SOCIAL_SIGN_UP = "socialSignUp"
        const val DASH_BOARD_LIST = "getDashBoardAllList"
        const val GET_MENU_LIST = "getMenuList"
        const val GET_RESTAURANT_DETAILS = "getRestaurantDetails/{id}"
        const val GET_PRODUCT_BY_RESTRAUNT = "getProductByRestraunt"

        // Data Store Constants
        const val KEY_IS_FIRST_TIME = "isFirstTime"
        const val KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"

        // FB Fields
        const val KEY_FIELDS = "fields"
        const val VALUE_FIELDS = "id,email,first_name,last_name,picture.type(large)"

        // FB JSON data
        const val FIELD_FB_ID = "id"
        const val FIELD_FB_FIRST_NAME = "first_name"
        const val FIELD_FB_LAST_NAME = "last_name"
        const val FIELD_FB_EMAIL = "email"
        const val FIELD_FB_PICTURE = "picture"
        const val FIELD_FB_DATA = "data"
        const val FIELD_FB_URL = "url"

        // FB User Details
        const val KEY_FB_ACCESS_TOKEN = "fb_access_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_FULL_NAME = "user_full_name"
        const val KEY_USER_FIRST_NAME = "user_first_name"
        const val KEY_USER_LAST_NAME = "user_last_name"
        const val KEY_USER_PROFILE_URL = "user_profile_url"

        const val KEY_LOGGED_IN_USER_DATA = "logged_in_user_data"

        const val SOCIAL_TYPE_FB = 1
        const val SOCIAL_TYPE_GOOGLE = 2

        // API
        const val KEY_BEARER_TOKEN = "bearer_token"
    }


    object DataStore {
        val DATA = stringPreferencesKey("data")
        val SECURED_DATA = stringPreferencesKey("secured_data")
    }
}