package com.specindia.ecommerce.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.cart.addUpdateToCart.AddUpdateToCartResponse
import com.specindia.ecommerce.models.response.cart.getcart.GetCartResponse
import com.specindia.ecommerce.models.response.cart.removeFromCart.RemoveFromCartResponse
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.SearchResponse
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
class
HomeViewModel @Inject constructor(
    private val repository: EcommerceRepository,
) : ViewModel() {

    // Mutable Live Data
    private val _dashboardListResponse = MutableLiveData<NetworkResult<DashboardListResponse>>()
    private val _menuListResponse = MutableLiveData<NetworkResult<MenuListResponse>>()
    private val _restaurantDetailsResponse =
        MutableLiveData<NetworkResult<RestaurantDetailsResponse>>()

    private val _orderDetailsResponse = MutableLiveData<NetworkResult<OrderDetailsResponse>>()

    private val _productsByRestaurantResponse =
        MutableLiveData<NetworkResult<ProductsByRestaurantResponse>>()

    private val _viewAllProductsResponse =
        MutableLiveData<NetworkResult<ViewAllData>>()

    private val _viewAllRestaurantResponse = MutableLiveData<NetworkResult<AllRestaurant>>()

    private val _searchResponse = MutableLiveData<NetworkResult<SearchResponse>>()

    private val _getCartResponse = MutableLiveData<NetworkResult<GetCartResponse>>()

    private val _addUpdateToCartResponse = MutableLiveData<NetworkResult<AddUpdateToCartResponse>>()

    private val _removeFromCartResponse = MutableLiveData<NetworkResult<RemoveFromCartResponse>>()

    // Live Data
    val dashboardListResponse: LiveData<NetworkResult<DashboardListResponse>>
        get() = _dashboardListResponse

    val menuListResponse: LiveData<NetworkResult<MenuListResponse>>
        get() = _menuListResponse

    val restaurantDetailsResponse: LiveData<NetworkResult<RestaurantDetailsResponse>>
        get() = _restaurantDetailsResponse

    val productsByRestaurant: LiveData<NetworkResult<ProductsByRestaurantResponse>>
        get() = _productsByRestaurantResponse

    val orderDetailsResponse: LiveData<NetworkResult<OrderDetailsResponse>>
        get() = _orderDetailsResponse

    val viewAllProductsResponse: LiveData<NetworkResult<ViewAllData>>
        get() = _viewAllProductsResponse

    val viewAllRestaurantResponse: LiveData<NetworkResult<AllRestaurant>>
        get() = _viewAllRestaurantResponse

    val searchResponse: LiveData<NetworkResult<SearchResponse>>
        get() = _searchResponse

    val getCartResponse: LiveData<NetworkResult<GetCartResponse>>
        get() = _getCartResponse

    val addUpdateToCart: LiveData<NetworkResult<AddUpdateToCartResponse>>
        get() = _addUpdateToCartResponse

    val removeFromCart: LiveData<NetworkResult<RemoveFromCartResponse>>
        get() = _removeFromCartResponse

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
        viewModelScope.launch {
            _restaurantDetailsResponse.postValue(NetworkResult.Loading())
            //delay(STATIC_DELAY)
            repository.getRestaurantDetails(headerMap, id).collect { values ->
                _restaurantDetailsResponse.value = values
            }
        }

    // Call Restaurant's Products api
    fun getProductsByRestaurant(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _productsByRestaurantResponse.postValue(NetworkResult.Loading())
            //delay(STATIC_DELAY)
            repository.getProductsByRestaurant(headerMap, parameters).collect { values ->
                _productsByRestaurantResponse.value = values
            }
        }

    // Call Order Details api
    fun getOrderDetails(headerMap: Map<String, String>, id: Int) =
        viewModelScope.launch {
            _orderDetailsResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getOrderDetails(headerMap, id).collect { values ->
                _orderDetailsResponse.value = values
            }
        }

    // Call View All Products api
    fun getAllProducts(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _viewAllProductsResponse.postValue(NetworkResult.Loading())
            delay(STATIC_DELAY)
            repository.getAllProducts(headerMap, parameters).collect { values ->
                _viewAllProductsResponse.value = values
            }
        }

    // Call View All Restaurant api
    fun getAllRestaurants(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _viewAllRestaurantResponse.postValue(NetworkResult.Loading())
            //delay(STATIC_DELAY)
            repository.getAllRestaurants(headerMap, parameters).collect { values ->
                _viewAllRestaurantResponse.value = values
            }
        }

    // Call Search api
    fun getSearch(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _searchResponse.postValue(NetworkResult.Loading())
            repository.getSearch(headerMap, parameters).collect { values ->
                _searchResponse.value = values
            }
        }

    // Call Get Cart api
    fun getCart(headerMap: Map<String, String>) =
        viewModelScope.launch {
            //delay(STATIC_DELAY)
            _getCartResponse.postValue(NetworkResult.Loading())
            repository.getCart(headerMap).collect { values ->
                _getCartResponse.value = values
            }
        }

    // Call Add Update To Cart api
    fun addUpdateToCart(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            //delay(STATIC_DELAY)
            _addUpdateToCartResponse.postValue(NetworkResult.Loading())
            repository.addUpdateToCart(headerMap, parameters).collect { values ->
                _addUpdateToCartResponse.value = values
            }
        }

    // Call Remove From Cart api
    fun removeFromCart(headerMap: Map<String, String>, parameters: String) =
        viewModelScope.launch {
            _addUpdateToCartResponse.postValue(NetworkResult.Loading())
            repository.addUpdateToCart(headerMap, parameters).collect { values ->
                _addUpdateToCartResponse.value = values
            }
        }
}