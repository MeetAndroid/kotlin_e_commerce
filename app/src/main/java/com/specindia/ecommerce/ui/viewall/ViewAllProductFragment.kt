package com.specindia.ecommerce.ui.viewall

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentViewAllProductBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.product.RestaurantItems
import com.specindia.ecommerce.models.response.home.product.ViewAllItems
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.ui.viewall.adapter.ViewAllAdapter
import com.specindia.ecommerce.ui.viewall.adapter.ViewAllRestaurantAdapter
import com.specindia.ecommerce.util.*
import com.specindia.ecommerce.util.Constants.Companion.CATEGORY
import com.specindia.ecommerce.util.Constants.Companion.RESTAURANT
import com.specindia.ecommerce.util.Constants.Companion.TOP_DISHES

/**
 * Show all product,restaurant when you click view all in home fragment.
 * it click like show Top dishes view all, show top product
 * when click restaurant view all show all restaurant
 **/

class ViewAllProductFragment : Fragment(), ViewAllAdapter.OnViewAllClickListener {

    private val args: ViewAllProductFragmentArgs by navArgs()
    var title = ""
    private lateinit var data: AuthResponseData
    private lateinit var binding: FragmentViewAllProductBinding

    private lateinit var viewAllAdapter: ViewAllAdapter
    private lateinit var viewAllRestaurantAdapter: ViewAllRestaurantAdapter
    private lateinit var customProgressDialog: AlertDialog
    private var viewAllList: ArrayList<ViewAllItems>? = null
    private var viewAllRestaurantList: ArrayList<RestaurantItems>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentViewAllProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewAllList = ArrayList()
        viewAllRestaurantList = ArrayList()

        when (args.tag) {
            TOP_DISHES -> title = TOP_DISHES
            RESTAURANT -> title = RESTAURANT
            CATEGORY -> title = CATEGORY
        }
        setUpHeader()
        setUpProgressDialog()
        setUpHeaderItemClick()

        //get user login response data
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        //call view all api
        callViewALL(data, false)

        //show top dishes list
        if (title == TOP_DISHES) {
            observeViewAllProducts()
        }

        //show top restaurant list
        if (title == RESTAURANT) {
            observeViewAllRestaurant()
        }

        //swipe to refresh and api call
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callViewALL(data, true)
        }
    }

    //setup header component hide and show as per requirement
    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = title
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                frShoppingCart.visible(false)
            }
        }
    }

    // when click any component for header
    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }

    //loading progress dialog
    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    // Call Products By Restaurant api
    private fun callViewALL(data: AuthResponseData, isSwipeToRefresh: Boolean) {

        if (title == TOP_DISHES) {
            if ((activity as HomeActivity).homeViewModel.viewAllProductsResponse.value == null || isSwipeToRefresh) {
                val parameter = Parameters(
                    pageNo = 1,
                    limit = 10
                )
                (activity as HomeActivity).homeViewModel.getAllProducts(
                    getHeaderMap(
                        data.token,
                        true
                    ),
                    Gson().toJson(parameter)
                )
            }

        } else {
            if ((activity as HomeActivity).homeViewModel.viewAllRestaurantResponse.value == null || isSwipeToRefresh) {
                val parameter1 = Parameters(
                    pageNo = 1,
                    limit = 10
                )
                (activity as HomeActivity).homeViewModel.getAllRestaurants(
                    getHeaderMap(
                        data.token,
                        true
                    ),
                    Gson().toJson(parameter1)
                )
            }
        }
    }

    // Observe Products By Restaurant Response
    private fun observeViewAllProducts() {
        (activity as HomeActivity).homeViewModel.viewAllProductsResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { it ->
                        with(binding) {
                            rvViewAll.layoutManager = LinearLayoutManager(
                                requireActivity(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            viewAllList?.clear()
                            it.data?.toList()?.let { it1 -> viewAllList?.addAll(it1) }
                            viewAllAdapter = viewAllList?.let { it1 ->
                                ViewAllAdapter(
                                    it1,
                                    this@ViewAllProductFragment
                                )
                            }!!
                            rvViewAll.adapter = viewAllAdapter
                            viewAllAdapter.notifyDataSetChanged()

                        }
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

    // Observe Restaurant Response
    private fun observeViewAllRestaurant() {
        (activity as HomeActivity).homeViewModel.viewAllRestaurantResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { it ->
                        with(binding) {
                            rvViewAll.layoutManager = LinearLayoutManager(
                                requireActivity(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            viewAllRestaurantList?.clear()
                            it.data?.toList()?.let { it1 -> viewAllRestaurantList?.addAll(it1) }
                            viewAllRestaurantAdapter = viewAllRestaurantList?.let { it1 ->
                                ViewAllRestaurantAdapter(
                                    it1
                                )
                            }!!
                            rvViewAll.adapter = viewAllRestaurantAdapter
                            viewAllRestaurantAdapter.notifyDataSetChanged()

                            viewAllRestaurantAdapter.setOnItemClickListener {
                                if (!requireActivity().isConnected) {
                                    view?.let { it1 ->
                                        showMaterialSnack(
                                            requireContext(),
                                            it1,
                                            getString(R.string.message_no_internet_connection)
                                        )
                                    }

                                } else {
                                    view?.findNavController()
                                        ?.navigate(
                                            ViewAllProductFragmentDirections.actionViewAllProductFragmentToRestaurantDetailsFragment(
                                                it.id!!
                                            )
                                        )
                                }

                            }
                        }
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

    //show message dialog when response any throw error
    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
    }

    //when click any item to get specific position
    override fun onItemClick(position: Int) {
        Log.e("POSITION", position.toString())
    }
}