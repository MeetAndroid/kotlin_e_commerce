package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.specindia.ecommerce.models.response.home.DashboardListResponse
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.CategoryListAdapter
import com.specindia.ecommerce.ui.adapters.PopularRestaurantsAdapter
import com.specindia.ecommerce.ui.adapters.TopProductsAdapter
import com.specindia.ecommerce.util.SnapHelper
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.showLongToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var loggedInUserName: String = ""

    private lateinit var restaurantsAdapter: PopularRestaurantsAdapter
    private lateinit var topProductAdapter: TopProductsAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            shimmerViewTopProducts.startShimmer()
            shimmerViewPopularRestaurants.startShimmer()
            shimmerCategoryList.startShimmer()
        }

    }

    override fun onPause() {
        binding.apply {
            shimmerViewPopularRestaurants.stopShimmer()
            shimmerViewTopProducts.stopShimmer()
            shimmerCategoryList.stopShimmer()
        }

        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeader()
        setUpHeaderItemClick()

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        val data = Gson().fromJson(userData, AuthResponseData::class.java)

        getUserDetails(data)
        setUpRecyclerView()
        callDashBoardListApi(data)
        observeResponse()

        restaurantsAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.name} clicked")
        }

        topProductAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.productName} clicked")
        }

        categoryListAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.name} clicked")
        }

        binding.btnHomeMenuDetails.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_homeDetailsFragment)
        }
    }


    // Init recycler view
    private fun setUpRecyclerView() {

        // Top Restaurants
        restaurantsAdapter = PopularRestaurantsAdapter()

        val linearSnapHelper: LinearSnapHelper = SnapHelper()

        binding.rvPopularRestaurants.apply {
            adapter = restaurantsAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
        linearSnapHelper.attachToRecyclerView(binding.rvPopularRestaurants)

        // Top Products
        topProductAdapter = TopProductsAdapter()
        binding.rvTopProducts.apply {
            adapter = topProductAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Category
        categoryListAdapter = CategoryListAdapter()
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
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeMenuScreenHeader) {
                ivShoppingCart.setOnClickListener {
                    requireActivity().showLongToast("Add to cart")
                }
            }
        }
    }

    private fun callDashBoardListApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getDashboardList(getHeaderMap(data.token, true))
    }


    private fun observeResponse() {
        (activity as HomeActivity).homeViewModel.dashboardListResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { dashboardListResponse ->
                        stopShimmerEffect(binding)
                        setUpTopDishUI(binding, dashboardListResponse)
                        setUpPopularRestaurantUI(binding, dashboardListResponse)
                        setUpCategoryListUI(binding, dashboardListResponse)
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


    private fun stopShimmerEffect(binding: FragmentHomeBinding) {
        binding.apply {
            shimmerViewPopularRestaurants.stopShimmer()
            shimmerViewPopularRestaurants.visible(false)
            shimmerViewTopProducts.stopShimmer()
            shimmerViewTopProducts.visible(false)
            shimmerCategoryList.stopShimmer()
            shimmerCategoryList.visible(false)
        }
    }

    private fun setUpTopDishUI(
        binding: FragmentHomeBinding,
        dashboardListResponse: DashboardListResponse
    ) {
        binding.apply {
            if (dashboardListResponse.data.topProduct.size > 0) {
                headerTopDishes.listHeader.visible(true)
                headerTopDishes.tvTitle.text =
                    getString(R.string.top_dishes)
                topProductAdapter.differ.submitList(dashboardListResponse.data.topProduct.toList())

                headerTopDishes.tvViewAll.setOnClickListener {
                    requireActivity().showLongToast("View All Top Dishes")
                }
            } else {
                headerTopDishes.listHeader.visible(false)
            }
        }
    }

    private fun setUpPopularRestaurantUI(
        binding: FragmentHomeBinding,
        dashboardListResponse: DashboardListResponse
    ) {
        binding.apply {
            if (dashboardListResponse.data.popularRestaurents.size > 0) {
                headerPopularRestaurants.listHeader.visible(true)
                headerPopularRestaurants.tvTitle.text =
                    getString(R.string.popular_restaurants)
                restaurantsAdapter.differ.submitList(dashboardListResponse.data.popularRestaurents.toList())

                headerPopularRestaurants.tvViewAll.setOnClickListener {
                    requireActivity().showLongToast("View All Popular Restaurants")
                }
            } else {
                headerPopularRestaurants.listHeader.visible(false)
            }
        }

    }


    private fun setUpCategoryListUI(
        binding: FragmentHomeBinding,
        dashboardListResponse: DashboardListResponse
    ) {
        binding.apply {
            if (dashboardListResponse.data.categoryList.size > 0) {
                headerCategoryList.listHeader.visible(true)
                headerCategoryList.tvTitle.text =
                    getString(R.string.top_categories)
                categoryListAdapter.differ.submitList(dashboardListResponse.data.categoryList.toList())

                headerCategoryList.tvViewAll.setOnClickListener {
                    requireActivity().showLongToast("View All Top Categories")
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
}

