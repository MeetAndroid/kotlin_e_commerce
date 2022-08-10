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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentOrderHistoryBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.AuthActivity
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.OrderHistoryAdapter
import com.specindia.ecommerce.ui.adapters.ViewAllAdapter
import com.specindia.ecommerce.util.*

class OrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private var orderList: ArrayList<OrderData>? = null
    lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderList = ArrayList()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
        setUpHeader()
        setUpHeaderItemClick()
        setUpProgressDialog()
        callOrderListApi()
        setObserver()
    }

    private fun setUpHeader() {
        with(binding) {
            with(orderHistoryScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(com.specindia.ecommerce.R.string.order_history)
                ivBack.visible(false)
                ivFavorite.visible(false)
                ivShoppingCart.visible(false)
                ivSearch.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(orderHistoryScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }


    // ============== Call Order List api
    private fun callOrderListApi() {
        customProgressDialog.show()
        val parameter = Parameters(
            pageNo = 1,
            limit = 10
        )

        (activity as HomeActivity).homeViewModel.getOrderList(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )

    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    private fun setObserver() {
        (activity as HomeActivity).homeViewModel.orderListResponse.observe(viewLifecycleOwner) { response ->


            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.hide()
                    response.data?.let { it ->
                        with(binding) {
                            orderList = it.data?.toList() as ArrayList<OrderData>?
                            orderHistoryAdapter = OrderHistoryAdapter(orderList!!)
                            rvOrderHistory.adapter = orderHistoryAdapter
                        }
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

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
    }
}