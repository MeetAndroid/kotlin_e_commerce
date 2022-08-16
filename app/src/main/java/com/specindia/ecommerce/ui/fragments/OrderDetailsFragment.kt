package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentOrderDetailsBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.order.OrderDetailsData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var data: AuthResponseData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        callOrderDetailsApi(data)
        observeOrderDetailsResponse()

        binding.clOrders.setOnClickListener {
            view.findNavController()
                .navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToCheckOutFragment())
        }

       // (activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()
    }

    private fun setUpData(data: OrderDetailsData) {
        binding.apply {
            Glide.with(binding.ivProductImage)
                .load("https://picsum.photos/200")
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.ic_dialog_alert)
                .into(binding.ivProductImage)

            tvProductName.text = "Mulberry Pizza by Josh"
            tvProductPrice.text = "250.00 Rs"
            tvOrderCount.text = "2"

            tvDiscount.text = "4"
            tvDeliveryCost.text = data.extraCharges
            tvSubTotal.text = data.subtotal
            tvTotal.text = data.total

        }

    }


    private fun setUpHeader() {
        with(binding) {
            with(orderDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.order_details)
                ivBack.visible(true)
                ivFavorite.visible(false)
                frShoppingCart.visible(false)
                ivSearch.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(orderDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }


    // Call Order Details API
    private fun callOrderDetailsApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getOrderDetails(
            getHeaderMap(
                data.token,
                true
            ),
            id = 1
        )
    }

    // Observe Order Details Response
    private fun observeOrderDetailsResponse() {
        (activity as HomeActivity).homeViewModel.orderDetailsResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { orderData ->
                        setUpData(orderData.data)
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
}