package com.specindia.ecommerce.util

import androidx.datastore.preferences.core.stringPreferencesKey

class Constants {
    companion object {
        const val NEWS_API_KEY = "8be6b5fd90574ef9bf33c38f65309b6c"
        const val NEWS_BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20

        // =================== Ecommerce App Constants

        const val STATIC_DELAY = 2000L

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
        const val GET_ALL_PRODUCT = "getAllProducts"
        const val GET_ALL_RESTAURANT = "getRestraunts"

        // Search
        const val SEARCH_FOOD = "searchFood"

        // Cart
        const val GET_CART = "getCart"
        const val REMOVE_FROM_CART = "removeFromCart"
        const val ADD_UPDATE_TO_CART = "addUpdateToCart"

        // Order
        const val CREATE_ORDER = "createOrder"
        const val GET_ORDER_DETAILS = "getOrderDetails/{id}"
        const val GET_ORDER_LIST = "getOrderList"

        // Address
        const val ADD_OR_UPDATE_ADDRESS = "addOrUpdateAddress"
        const val GET_ADDRESS = "getAddress"
        const val SET_PRIMARY_ADDRESS = "setPrimaryAddress"
        const val REMOVE_ADDRESS = "removeAddress"

        // Data Store Constants
        const val KEY_IS_FIRST_TIME = "isFirstTime"
        const val KEY_IS_USER_LOGGED_IN = "isUserLoggedIn"
        const val KEY_EXISTING_RESTAURANT_ID_OF_CART = "existingRestaurantIdOfCart"
        const val KEY_CART_ITEM_COUNT = "cartItemCount"
        const val KEY_PRIMARY_ADDRESS_INFO = "primaryAddressInfo"

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
        const val KEY_FAV_RESTAURANTS = "key_fav_restaurants"

        const val SOCIAL_TYPE_FB = 1
        const val SOCIAL_TYPE_GOOGLE = 2

        const val TOP_DISHES = "Top Dishes"
        const val RESTAURANT = "Restaurant"
        const val CATEGORY = "Category"
        const val SEARCH = "Search"

        // Bundle Keys
        const val REQUEST_FROM_RESTAURANT_DETAILS = "request_from_restaurant_details"
        const val IS_FROM_PRODUCT_DETAILS = "is_from_product_details"

        const val LOCATION_REQUEST = 1000
        const val GPS_REQUEST = 1001
        const val LOCATION_PERMISSION_REQUEST_CODE = 2000
        const val REQUEST_CHECK_SETTINGS = 3000
    }


    object DataStore {
        val DATA = stringPreferencesKey("data")
        val SECURED_DATA = stringPreferencesKey("secured_data")
    }

    object DateFormat {
        const val INPUT_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
        const val OUTPUT_FORMAT = "dd-MM-yyyy hh:mm"
    }

    enum class ScreenNameToNavigateCartList {
        HOME_SCREEN,
        RESTAURANT_DETAILS_SCREEN,
        PRODUCT_DETAILS_SCREEN,
        FOOD_MENU_SCREEN,
        MORE_SCREEN
    }
}