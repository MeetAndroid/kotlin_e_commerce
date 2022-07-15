package com.specindia.ecommerce.util

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

        const val CONTENT_TYPE_JSON="Content-Type: application/json"

        // End Point
        const val SIGN_UP = "signUp"
        const val LOGIN = "login"

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
    }
}