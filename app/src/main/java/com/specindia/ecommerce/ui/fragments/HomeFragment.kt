package com.specindia.ecommerce.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentHomeBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.Category
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.models.response.home.PopularRestaurent
import com.specindia.ecommerce.models.response.home.TopProduct
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.CategoryListAdapter
import com.specindia.ecommerce.ui.adapters.PopularRestaurantsAdapter
import com.specindia.ecommerce.ui.adapters.TopProductsAdapter
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var loggedInUserName: String = ""

    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var topProductAdapter: TopProductsAdapter
    private lateinit var restaurantsAdapter: PopularRestaurantsAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
        //(activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        getUserDetails(data)
        setUpRecyclerView()

        if ((activity as HomeActivity).homeViewModel.dashboardListResponse.value == null) {
            callDashBoardListApi(data)
        }

        observeResponse()
        callGetCartApi()
        observeGetCartResponse()

        topProductAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.productName} clicked")

        }
        restaurantsAdapter.setOnItemClickListener {
            view.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToRestaurantDetailsFragment(it.id))
        }
        categoryListAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.name} clicked")
        }

        binding.tvSearch.setOnClickListener {
            it.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callDashBoardListApi(data)
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
                ivShoppingCart.visible(true)
                ivSearch.visible(true)

                ivSearch.setOnClickListener {
                    view?.findNavController()
                        ?.navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                }
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeMenuScreenHeader) {
                ivShoppingCart.setOnClickListener {
                    requireActivity().showLongToast("Cart List")
                    view?.findNavController()
                        ?.navigate(HomeFragmentDirections.actionHomeFragmentToCartListFragment())
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
                    customProgressDialog.hide()
                    response.data?.let { dashboardListResponse ->
                        setUpTopDishUI(binding, dashboardListResponse)
                        setUpPopularRestaurantUI(binding, dashboardListResponse)
                        setUpCategoryListUI(binding, dashboardListResponse)
                    }
                }
                is NetworkResult.Error -> {
                    customProgressDialog.hide()
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
                    view?.findNavController()?.navigate(
                        HomeFragmentDirections.actionHomeFragmentToViewAllProductFragment(Constants.TOP_DISHES)
                    )
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
                    view?.findNavController()?.navigate(
                        HomeFragmentDirections.actionHomeFragmentToViewAllProductFragment(Constants.RESTAURANT)
                    )
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
                    view?.findNavController()?.navigate(
                        HomeFragmentDirections.actionHomeFragmentToViewAllProductFragment(Constants.CATEGORY)
                    )
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
    @SuppressLint("LongLogTag")
    private fun observeGetCartResponse() {
        (activity as HomeActivity).homeViewModel.getCartResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.hide()
                    response.data?.let { cartListResponse ->
                        saveExistingRestaurantIdOfCart(cartListResponse, (activity as HomeActivity))
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
}

