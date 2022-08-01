package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.order.OrderDetailsResponse
import com.specindia.ecommerce.models.response.home.product.AllRestaurant
import com.specindia.ecommerce.models.response.home.product.ViewAllData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantResponse
import com.specindia.ecommerce.models.response.home.restaurantDetails.RestaurantDetailsResponse
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.repository.EcommerceRepository
import com.specindia.ecommerce.util.Constants.Companion.STATIC_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: EcommerceRepository,
) : ViewModel() {

    // Mutable Live Data
    private val _dashboardListResponse = MutableLiveData<NetworkResult<DashboardListResponse>>()
    private val _menuListResponse = MutableLiveData<NetworkResult<MenuListResponse>>()
    private val _restaurantDetailsResponse =
        MutableLiveData<NetworkResult<RestaurantDetailsResponse>>()

    private val _orderDetailsResponse =
        MutableLiveData<NetworkResult<OrderDetailsResponse>>()

    private val _productsByRestauranResponse =
        MutableLiveData<NetworkResult<ProductsByRestaurantResponse>>()

    private val _viewAllResponse =
        MutableLiveData<NetworkResult<ViewAllData>>()

    private val _viewAllRestaurantResponse =
        MutableLiveData<NetworkResult<AllRestaurant>>()

    // Live Data
    val dashboardListResponse: LiveData<NetworkResult<DashboardListResponse>>
        get() = _dashboardListResponse

    val menuListResponse: LiveData<NetworkResult<MenuListResponse>>
        get() = _menuListResponse

    val restaurantDetailsResponse: LiveData<NetworkResult<RestaurantDetailsResponse>>
        get() = _restaurantDetailsResponse

    val productsByRestaurant: LiveData<NetworkResult<ProductsByRestaurantResponse>>
        get() = _productsByRestauranResponse

    val orderDetailsResponse: LiveData<NetworkResult<OrderDetailsResponse>>
        get() = _orderDetailsResponse

    val viewAllResponse: LiveData<NetworkResult<ViewAllData>>
        get() = _viewAllResponse

    val viewAllRestaurantResponse: LiveData<NetworkResult<AllRestaurant>>
        get() = _viewAllRestaurantResponse

    // Call dashboard list api
    fun getDashboardList(headerMap: Map<String, String>) =
        viewModelScope.launch() {
            _dashboardListResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getDashboardList(headerMap).collect { values ->
                _dashboardListResponse.value = values
            }
        }

    // Call menu list api
    fun getMenuList(headerMap: Map<String, String>) =
        viewModelScope.launch() {
            _menuListResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getMenuList(headerMap).collect { values ->
                _menuListResponse.value = values
            }
        }

    // Call Restaurant Details api
    fun getRestaurantDetails(headerMap: Map<String, String>, id: Int) =
        viewModelScope.launch() {
            _restaurantDetailsResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getRestaurantDetails(headerMap, id).collect { values ->
                _restaurantDetailsResponse.value = values
            }
        }

    // Call Restaurant's Products api
    fun getProductsByRestaurant(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch() {
            _productsByRestauranResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getProductsByRestaurant(headerMap, parameters).collect { values ->
                _productsByRestauranResponse.value = values
            }
        }

    // Call Order Details api
    fun getOrderDetails(headerMap: Map<String, String>, id: Int) =
        viewModelScope.launch() {
            _orderDetailsResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getOrderDetails(headerMap, id).collect { values ->
                _orderDetailsResponse.value = values
            }
        }

    // Call View All api
    fun getViewAll(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch() {
            _viewAllResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getViewAll(headerMap, parameters).collect { values ->
                _viewAllResponse.value = values
            }
        }

    // Call View All Restaurant api
    fun getAllRestaurant(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch() {
            _viewAllRestaurantResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getAllRestaurant(headerMap, parameters).collect { values ->
                _viewAllRestaurantResponse.value = values
            }
        }
}