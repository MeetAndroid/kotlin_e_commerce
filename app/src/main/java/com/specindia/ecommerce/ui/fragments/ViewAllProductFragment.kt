package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentViewAllProductBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.product.AllRestaurant
import com.specindia.ecommerce.models.response.home.product.RestaurantItems
import com.specindia.ecommerce.models.response.home.product.ViewAllItems
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.ViewAllAdapter
import com.specindia.ecommerce.ui.adapters.ViewAllRestaurantAdapter
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.visible

class ViewAllProductFragment : Fragment(), ViewAllAdapter.OnViewAllClickListener {

    private val args: ViewAllProductFragmentArgs by navArgs()
    var title = ""
    private lateinit var data: AuthResponseData
    private lateinit var binding: FragmentViewAllProductBinding

    private lateinit var viewAllAdapter: ViewAllAdapter
    private lateinit var viewAllRestaurantAdapter: ViewAllRestaurantAdapter
    private var viewAllList: ArrayList<ViewAllItems>? = null
    private var viewAllRestaurantList: ArrayList<RestaurantItems>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllProductBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewAllList = ArrayList()
        viewAllRestaurantList = ArrayList()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        when (args.tag) {
            Constants.TOP_DISHES -> {
                title = Constants.TOP_DISHES
            }
            Constants.RESTAURANT -> {
                title = Constants.RESTAURANT
            }
            Constants.CATEGORY -> {
                title = Constants.CATEGORY
            }
        }
        setUpHeader()
        setUpHeaderItemClick()
        callViewALl()
        observeViewAllProducts()
        observeViewAllRestaurant()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callViewALl()
        }

    }

    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = title
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                ivShoppingCart.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }


    // Call Products By Restaurant api
    private fun callViewALl() {
        if (title == Constants.TOP_DISHES) {
            val parameter = Parameters(
                pageNo = 1,
                limit = 10
            )
            (activity as HomeActivity).homeViewModel.getViewAll(
                getHeaderMap(
                    data.token,
                    true
                ),
                Gson().toJson(parameter)
            )
        } else {

            val parameter1 = Parameters(
                pageNo = 1,
                limit = 10
            )
            (activity as HomeActivity).homeViewModel.getAllRestaurant(
                getHeaderMap(
                    data.token,
                    true
                ),
                Gson().toJson(parameter1)
            )

        }


    }

    // Observe Products By Restaurant Response
    private fun observeViewAllProducts() {
        (activity as HomeActivity).homeViewModel.viewAllResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
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
                            viewAllAdapter.showShimmer = false
                            viewAllAdapter.notifyDataSetChanged()

                        }
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

    // Observe Restaurant Response
    private fun observeViewAllRestaurant() {
        (activity as HomeActivity).homeViewModel.viewAllRestaurantResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
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
                            viewAllRestaurantAdapter.showShimmer = false
                            viewAllRestaurantAdapter.notifyDataSetChanged()

                            viewAllRestaurantAdapter.setOnItemClickListener {
                                view?.findNavController()?.navigate(ViewAllProductFragmentDirections.actionViewAllProductFragmentToRestaurantDetailsFragment(
                                    it.id!!
                                ))
                            }
                        }
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

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
    }

    override fun onItemClick(position: Int) {
        Log.e("POSITION", position.toString())
    }
}