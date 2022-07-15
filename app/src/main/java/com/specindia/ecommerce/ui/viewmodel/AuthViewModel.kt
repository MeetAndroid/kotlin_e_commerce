package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.login.LoginResponse
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.repository.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: EcommerceRepository
) : ViewModel() {

    // Mutable Live Data
    private val _registrationResponse = MutableLiveData<NetworkResult<RegistrationResponse>>()
    private val _loginResponse = MutableLiveData<NetworkResult<LoginResponse>>()

    // Live Data
    val registrationResponse: LiveData<NetworkResult<RegistrationResponse>>
        get() = _registrationResponse

    val loginResponse: LiveData<NetworkResult<LoginResponse>>
        get() = _loginResponse


    // Call Registration API
    fun doRegistration(parameters: String) = viewModelScope.launch() {
        repository.doRegistration(parameters).collect { values ->
            _registrationResponse.value = values
        }
    }

    // Call Login API
    fun doLogin(parameters: String) = viewModelScope.launch() {
        repository.doLogin(parameters).collect { values ->
            _loginResponse.value = values
        }
    }

}