package com.specindia.ecommerce.ui.fragments

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
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.PopularRestaurantsAdapter
import com.specindia.ecommerce.ui.adapters.TopProductsAdapter
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var customProgressDialog: AlertDialog
    private var loggedInUserName: String = ""

    private lateinit var restaurantsAdapter: PopularRestaurantsAdapter
    private lateinit var topProductAdapter: TopProductsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeader()
        setUpHeaderItemClick()

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        val data = Gson().fromJson(userData, AuthResponseData::class.java)

        setUpProgressDialog()
        getUserDetails(data)
        setUpRecyclerView()
        callDashBoardListApi(data)
        observeResponse()

        restaurantsAdapter.setOnItemClickListener {
            requireActivity().showLongToast("Restaurant clicked")
        }

        topProductAdapter.setOnItemClickListener {
            requireActivity().showLongToast("Top Product clicked")
        }

        binding.btnHomeMenuDetails.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_homeDetailsFragment)
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
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
        customProgressDialog.show()
        (activity as HomeActivity).homeViewModel.getDashboardList(getHeaderMap(data.token, true))
    }


    private fun observeResponse() {
        (activity as HomeActivity).homeViewModel.dashboardListResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { dashboardListResponse ->

                        if (dashboardListResponse.data.popularRestaurents.size > 0) {
                            restaurantsAdapter.differ.submitList(dashboardListResponse.data.popularRestaurents.toList())
                        }

                        if (dashboardListResponse.data.topProduct.size > 0) {
                            topProductAdapter.differ.submitList(dashboardListResponse.data.topProduct.toList())
                        }
                    }
                    customProgressDialog.hide()
                }
                is NetworkResult.Error -> {
                    customProgressDialog.hide()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                }
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

