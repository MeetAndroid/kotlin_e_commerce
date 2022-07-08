package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.datastore.abstraction.DataStoreRepository
import com.specindia.ecommerce.util.Constants.Companion.KEY_IS_FIRST_TIME
import com.specindia.ecommerce.util.Constants.Companion.KEY_IS_USER_LOGGED_IN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val repository: DataStoreRepository
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

}