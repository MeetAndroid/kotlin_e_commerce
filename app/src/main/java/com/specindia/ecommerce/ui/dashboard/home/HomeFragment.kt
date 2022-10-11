package com.specindia.ecommerce.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentHomeBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.Category
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.PopularRestaurent
import com.specindia.ecommerce.models.response.home.TopProduct
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.ui.dashboard.home.adapter.CategoryListAdapter
import com.specindia.ecommerce.ui.dashboard.home.adapter.PopularRestaurantsAdapter
import com.specindia.ecommerce.ui.dashboard.home.adapter.RecentProductsAdapter
import com.specindia.ecommerce.ui.dashboard.home.adapter.TopProductsAdapter
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var recentList = ArrayList<ProductsByRestaurantData>()

    private lateinit var binding: FragmentHomeBinding
    private var loggedInUserName: String = ""

    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var topProductAdapter: TopProductsAdapter
    private lateinit var restaurantsAdapter: PopularRestaurantsAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var recentProductsAdapter: RecentProductsAdapter

    private lateinit var topProductList: ArrayList<TopProduct>
    private lateinit var restaurantList: ArrayList<PopularRestaurent>
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentList = ArrayList()
        setUpHeader()
        setUpHeaderItemClick()
        //(activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()
        setUpProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            val userData = (activity as HomeActivity).dataStoreRepository.getString(Constants.KEY_LOGGED_IN_USER_DATA)
            data = Gson().fromJson(userData, AuthResponseData::class.java)
            getUserDetails(data)
            delay(2000)
            if ((activity as HomeActivity).homeViewModel.dashboardListResponse.value == null) {
                callDashBoardListApi(data)
            }
        }

        setUpRecyclerView()
        observeResponse()
        callGetCartApi()
        observeGetCartResponse()

        topProductAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.productName} clicked")

        }

        restaurantsAdapter.setOnItemClickListener {
            if (!requireActivity().isConnected) {
                showMaterialSnack(
                    requireContext(),
                    view,
                    getString(R.string.message_no_internet_connection)
                )
            } else {
                view.findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToRestaurantDetailsFragment(
                            it.id
                        )
                    )
            }

        }

        categoryListAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.name} clicked")
        }

        recentProductsAdapter.setOnItemClickListener {
            val bundle = Gson().toJson(it)
            view.findNavController()
                .navigate(
                    HomeFragmentDirections.actionSearchFragmentToProductDetailsFragment(
                        it.productId,
                        Constants.RECENT,
                        bundle
                    )
                )
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callDashBoardListApi(data)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toBase64()
        }


        val prefRecentList1 = (activity as HomeActivity).dataStoreViewModel.getRecentList()
        if (prefRecentList1 != null) {
            binding.headerRecentList.tvTitle.text =
                getString(R.string.recent)
            val arrayType = object : TypeToken<ArrayList<ProductsByRestaurantData>>() {}.type
            val prefRestaurantList: ArrayList<ProductsByRestaurantData> =
                Gson().fromJson(prefRecentList1, arrayType)

            recentList.addAll(prefRestaurantList.reversed())
            Log.e("RECENT_LIST", "DATA:- " + Gson().toJson(recentList))
        }


    }


    // Init recycler view
    private fun setUpRecyclerView() {
        topProductList = ArrayList()
        restaurantList = ArrayList()
        categoryList = ArrayList()
        // Top Products
        topProductAdapter = TopProductsAdapter(topProductList)
        binding.rvTopProducts.apply {
            adapter = topProductAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Top Restaurants
        restaurantsAdapter = PopularRestaurantsAdapter(restaurantList)

        val linearSnapHelper: LinearSnapHelper = SnapHelper()

        binding.rvPopularRestaurants.apply {
            adapter = restaurantsAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        linearSnapHelper.attachToRecyclerView(binding.rvPopularRestaurants)


        // Category
        categoryListAdapter = CategoryListAdapter(categoryList)
        binding.rvCategoryList.apply {
            adapter = categoryListAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Recent
        recentProductsAdapter = RecentProductsAdapter(recentList)
        binding.rvRecentList.apply {
            adapter = recentProductsAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

    }

    private fun getUserDetails(data: AuthResponseData) {
        loggedInUserName = data.firstName
        with(binding) {
            homeMenuScreenHeader.tvHeaderTitle.text =
                getString(R.string.title_good_morning_user, loggedInUserName)
        }
        Log.d(
            "FB", """
                    |id = ${data.id}
                    |First Name = ${data.firstName}
                    |Last Name =${data.lastName}
                    |Email =${data.email}
                    |Profile URL =${data.profileImage}
                """.trimMargin()
        )
    }

    private fun setUpHeader() {
        with(binding) {
            with(homeMenuScreenHeader) {
                tvHeaderTitle.visible(true)
                ivBack.visible(false)
                ivFavorite.visible(false)
                frShoppingCart.visible(true)
                ivSearch.visible(true)

                ivSearch.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        it.findNavController()
                            .navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                    }

                }

            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeMenuScreenHeader) {
                frShoppingCart.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )
                    } else {
                        view?.findNavController()
                            ?.navigate(HomeFragmentDirections.actionHomeFragmentToCartListFragment())
                    }

                }
            }
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    private fun callDashBoardListApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getDashboardList(getHeaderMap(data.token, true))
    }

    private fun observeResponse() {
        (activity as HomeActivity).homeViewModel.dashboardListResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { dashboardListResponse ->
                        setUpTopDishUI(binding, dashboardListResponse)
                        setUpPopularRestaurantUI(binding, dashboardListResponse)
                        setUpCategoryListUI(binding, dashboardListResponse)
                    }
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                    customProgressDialog.show()
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpTopDishUI(
        binding: FragmentHomeBinding,
        dashboardListResponse: DashboardListResponse,
    ) {
        binding.apply {
            topProductList.clear()
            if (dashboardListResponse.data.topProduct.isNotEmpty()) {
                headerTopDishes.listHeader.visible(true)
                headerTopDishes.tvViewAll.visible(true)
                headerTopDishes.tvTitle.text =
                    getString(R.string.top_dishes)


                topProductList.addAll(dashboardListResponse.data.topProduct.toList())
                topProductAdapter.notifyDataSetChanged()

                headerTopDishes.tvViewAll.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )
                    } else {
                        view?.findNavController()?.navigate(
                            HomeFragmentDirections.actionHomeFragmentToViewAllProductFragment(
                                Constants.TOP_DISHES
                            )
                        )
                    }

                }
            } else {
                headerTopDishes.listHeader.visible(false)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpPopularRestaurantUI(
        binding: FragmentHomeBinding,
        dashboardListResponse: DashboardListResponse,
    ) {
        binding.apply {
            restaurantList.clear()
            if (dashboardListResponse.data.popularRestaurents.isNotEmpty()) {
                headerPopularRestaurants.listHeader.visible(true)
                headerPopularRestaurants.tvViewAll.visible(true)
                headerPopularRestaurants.tvTitle.text =
                    getString(R.string.popular_restaurants)

                restaurantList.addAll(dashboardListResponse.data.popularRestaurents.toList())
                restaurantsAdapter.notifyDataSetChanged()

                headerPopularRestaurants.tvViewAll.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        view?.findNavController()?.navigate(
                            HomeFragmentDirections.actionHomeFragmentToViewAllProductFragment(
                                Constants.RESTAURANT
                            )
                        )
                    }

                }
            } else {
                headerPopularRestaurants.listHeader.visible(false)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpCategoryListUI(
        binding: FragmentHomeBinding,
        dashboardListResponse: DashboardListResponse,
    ) {
        binding.apply {
            categoryList.clear()
            if (dashboardListResponse.data.categoryList.isNotEmpty()) {
                headerCategoryList.listHeader.visible(true)
                headerCategoryList.tvViewAll.visible(false)
                headerCategoryList.tvTitle.text =
                    getString(R.string.top_categories)

                categoryList.addAll(dashboardListResponse.data.categoryList.toList())
                categoryListAdapter.notifyDataSetChanged()

                headerCategoryList.tvViewAll.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        view?.findNavController()?.navigate(
                            HomeFragmentDirections.actionHomeFragmentToViewAllProductFragment(
                                Constants.CATEGORY
                            )
                        )
                    }

                }
            } else {
                headerCategoryList.listHeader.visible(false)
            }
        }
    }

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
    }

    // ============== GET CART DATA
    private fun callGetCartApi() {
        Log.e("GetCart", "Calling...")
        customProgressDialog.show()
        (activity as HomeActivity).homeViewModel.getCart(
            getHeaderMap(
                data.token,
                true
            )
        )
    }

    // ============== Observe Cart Response
    @ExperimentalBadgeUtils
    private fun observeGetCartResponse() {
        (activity as HomeActivity).homeViewModel.getCartResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { cartListResponse ->
                        handleCartBadgeCount(
                            cartListResponse,
                            (activity as HomeActivity),
                            binding.homeMenuScreenHeader.frShoppingCart
                        )
                    }
                }
                is NetworkResult.Error -> {
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun toBase64() {
        val oriString = "bezkoder tutorial"
        val encodedString =
            Base64.getEncoder().withoutPadding().encodeToString(oriString.toByteArray())

        println(encodedString)

        val decodedBytes = Base64.getDecoder().decode(encodedString)
        val decodedString = String(decodedBytes)

        println(decodedString)
    }
}

