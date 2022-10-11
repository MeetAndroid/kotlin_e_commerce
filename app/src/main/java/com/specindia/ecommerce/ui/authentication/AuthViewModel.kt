package com.specindia.ecommerce.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.login.LoginResponse
import com.specindia.ecommerce.models.response.registration.RegistrationResponse
import com.specindia.ecommerce.models.response.social.SocialLoginResponse
import com.specindia.ecommerce.util.Constants.Companion.STATIC_DELAY
import com.specindia.ecommerce.repository.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: EcommerceRepository,
) : ViewModel() {

    // Mutable Live Data
    private val _registrationResponse = MutableLiveData<NetworkResult<RegistrationResponse>>()
    private val _loginResponse = MutableLiveData<NetworkResult<LoginResponse>>()
    private val _socialResponse = MutableLiveData<NetworkResult<SocialLoginResponse>>()

    // Live Data
    val registrationResponse: LiveData<NetworkResult<RegistrationResponse>>
        get() = _registrationResponse

    val loginResponse: LiveData<NetworkResult<LoginResponse>>
        get() = _loginResponse

    val socialResponse: LiveData<NetworkResult<SocialLoginResponse>>
        get() = _socialResponse


    // Call Registration API
    fun doRegistration(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _registrationResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.doRegistration(headerMap, parameters).collect { values ->
                _registrationResponse.value = values
            }
        }

    // Call Login API
    fun doLogin(headerMap: Map<String, String>, parameters: String) = viewModelScope.launch {
        _loginResponse.postValue(NetworkResult.Loading())
        delay(STATIC_DELAY)
        repository.doLogin(headerMap, parameters).collect { values ->
            _loginResponse.value = values
        }
    }

    // Call Social Login API (Facebook and GPlus)
    fun doSocialLogin(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _socialResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.doSocialLogin(headerMap, parameters).collect { values ->
                _socialResponse.value = values
            }
        }

}