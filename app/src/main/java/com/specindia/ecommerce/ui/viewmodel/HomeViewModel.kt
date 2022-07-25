package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.RestaurantDetailsResponse
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.repository.EcommerceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: EcommerceRepository
) : ViewModel() {

    // Mutable Live Data
    private val _dashboardListResponse = MutableLiveData<NetworkResult<DashboardListResponse>>()
    private val _menuListResponse = MutableLiveData<NetworkResult<MenuListResponse>>()
    private val _restaurantDetailsResponse =
        MutableLiveData<NetworkResult<RestaurantDetailsResponse>>()

    // Live Data
    val dashboardListResponse: LiveData<NetworkResult<DashboardListResponse>>
        get() = _dashboardListResponse

    val menuListResponse: LiveData<NetworkResult<MenuListResponse>>
        get() = _menuListResponse

    val restaurantDetailsResponse: LiveData<NetworkResult<RestaurantDetailsResponse>>
        get() = _restaurantDetailsResponse

    // Call dashboard list api
    fun getDashboardList(headerMap: Map<String, String>) =
        viewModelScope.launch() {
            repository.getDashboardList(headerMap).collect { values ->
                _dashboardListResponse.value = values
            }
        }

    // Call menu list api
    fun getMenuList(headerMap: Map<String, String>) =
        viewModelScope.launch() {
            repository.getMenuList(headerMap).collect { values ->
                _menuListResponse.value = values
            }
        }

    // Call Restaurant Details api
    fun getRestaurantDetails(headerMap: Map<String, String>,id:Int) =
        viewModelScope.launch() {
            repository.getRestaurantDetails(headerMap,id).collect { values ->
                _restaurantDetailsResponse.value = values
            }
        }
}