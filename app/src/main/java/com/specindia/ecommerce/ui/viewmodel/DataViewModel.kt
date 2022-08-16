package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.datastore.abstraction.DataStoreRepository
import com.specindia.ecommerce.util.Constants.Companion.KEY_CART_ITEM_COUNT
import com.specindia.ecommerce.util.Constants.Companion.KEY_EXISTING_RESTAURANT_ID_OF_CART
import com.specindia.ecommerce.util.Constants.Companion.KEY_FAV_RESTAURANTS
import com.specindia.ecommerce.util.Constants.Companion.KEY_FB_ACCESS_TOKEN
import com.specindia.ecommerce.util.Constants.Companion.KEY_IS_FIRST_TIME
import com.specindia.ecommerce.util.Constants.Companion.KEY_IS_USER_LOGGED_IN
import com.specindia.ecommerce.util.Constants.Companion.KEY_LOGGED_IN_USER_DATA
import com.specindia.ecommerce.util.Constants.Companion.KEY_USER_EMAIL
import com.specindia.ecommerce.util.Constants.Companion.KEY_USER_FIRST_NAME
import com.specindia.ecommerce.util.Constants.Companion.KEY_USER_FULL_NAME
import com.specindia.ecommerce.util.Constants.Companion.KEY_USER_ID
import com.specindia.ecommerce.util.Constants.Companion.KEY_USER_LAST_NAME
import com.specindia.ecommerce.util.Constants.Companion.KEY_USER_PROFILE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val repository: DataStoreRepository,
) : ViewModel() {

    fun saveIsFirstTime(value: Boolean) {
        viewModelScope.launch {
            repository.putBoolean(KEY_IS_FIRST_TIME, value)
        }
    }

    fun getIsFirstTime(): Boolean? = runBlocking {
        repository.getBoolean(KEY_IS_FIRST_TIME)
    }

    fun saveUserLoggedIn(value: Boolean) {
        viewModelScope.launch {
            repository.putBoolean(KEY_IS_USER_LOGGED_IN, value)
        }
    }

    fun getUserLoggedIn(): Boolean? = runBlocking {
        repository.getBoolean(KEY_IS_USER_LOGGED_IN)
    }

    fun saveUserId(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_USER_ID, value)
        }
    }

    fun getUserId(): String? = runBlocking {
        repository.getString(KEY_USER_ID)
    }

    fun saveFullName(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_USER_FULL_NAME, value)
        }
    }

    fun getFullName(): String? = runBlocking {
        repository.getString(KEY_USER_FULL_NAME)
    }

    fun saveFBAccessToken(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_FB_ACCESS_TOKEN, value)
        }
    }

    fun getFBAccessToken(): String? = runBlocking {
        repository.getString(KEY_FB_ACCESS_TOKEN)
    }

    fun saveEmail(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_USER_EMAIL, value)
        }
    }

    fun getEmail(): String? = runBlocking {
        repository.getString(KEY_USER_EMAIL)
    }

    fun saveFirstName(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_USER_FIRST_NAME, value)
        }
    }

    fun getFirstName(): String? = runBlocking {
        repository.getString(KEY_USER_FIRST_NAME)
    }

    fun saveLastName(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_USER_LAST_NAME, value)
        }
    }

    fun getLastName(): String? = runBlocking {
        repository.getString(KEY_USER_LAST_NAME)
    }

    fun saveProfileUrl(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_USER_PROFILE_URL, value)
        }
    }

    fun getProfileUrl(): String? = runBlocking {
        repository.getString(KEY_USER_PROFILE_URL)
    }


    // Save Login Response
    fun saveLoggedInUserData(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_LOGGED_IN_USER_DATA, value)
        }
    }

    fun getLoggedInUserData(): String? = runBlocking {
        repository.getString(KEY_LOGGED_IN_USER_DATA)
    }

    fun saveFavoriteRestaurantList(value: String) {
        viewModelScope.launch {
            repository.putString(KEY_FAV_RESTAURANTS, value)
        }
    }

    fun getFavoriteRestaurantList(): String? = runBlocking {
        repository.getString(KEY_FAV_RESTAURANTS)
    }

    fun saveExistingRestaurantIdOfCart(value: Int) {
        viewModelScope.launch {
            repository.putInt(KEY_EXISTING_RESTAURANT_ID_OF_CART, value)
        }
    }

    fun getExistingRestaurantIdFromCart(): Int? = runBlocking {
        repository.getInt(KEY_EXISTING_RESTAURANT_ID_OF_CART)
    }

    fun saveCartItemCount(value: Int) {
        viewModelScope.launch {
            repository.putInt(KEY_CART_ITEM_COUNT, value)
        }
    }


    fun getCartItemCount(): Int? = runBlocking {
        repository.getInt(KEY_CART_ITEM_COUNT)
    }
}

