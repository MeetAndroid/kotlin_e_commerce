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
import com.specindia.ecommerce.databinding.FragmentShippingAddressBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListData
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListResponse
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.ShippingAddressAdapter
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAddressFragment : Fragment(), ShippingAddressAdapter.OnShippingAddressClickListener {

    private lateinit var binding: FragmentShippingAddressBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var shippingAddressAdapter: ShippingAddressAdapter
    private lateinit var shippingAddressList: ArrayList<GetAddressListData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShippingAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeader()
        setUpHeaderItemClick()
        setUpButtonClick()
        setUpRecyclerView()
        setUpProgressDialog()

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        callGetAddressApi()
        observeGetAddressResponse(binding)
    }

    private fun setUpHeader() {
        with(binding) {
            with(shippingAddressScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.shipping_address)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                frShoppingCart.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(shippingAddressScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }

    private fun setUpButtonClick() {
        with(binding) {
            btnAddNewAddress.setOnClickListener {
                it.findNavController()
                    .navigate(ShippingAddressFragmentDirections.actionShippingAddressFragmentToSetLocationFragment())
            }

        }
    }

    private fun setUpRecyclerView() {
        // Addresses
        shippingAddressList = ArrayList()
        shippingAddressAdapter =
            ShippingAddressAdapter(shippingAddressList, this, (activity as HomeActivity))
        binding.rvShippingAddress.apply {
            adapter = shippingAddressAdapter
            setHasFixedSize(false)
            addItemDecoration(MarginDecoration(resources.getDimensionPixelSize(R.dimen.item_margin_16),
                false))
            isNestedScrollingEnabled = false
            layoutManager =
                LinearLayoutManager(requireActivity())
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
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

    private fun setUpAddressListUI(
        binding: FragmentShippingAddressBinding,
        addressListResponse: GetAddressListResponse,
    ) {
        binding.apply {
            shippingAddressList.clear()
            noDataFound.clNoDataFound.visible(true)
            nestedScrollview.visible(false)

            if (addressListResponse.data.isNotEmpty()) {
                nestedScrollview.visible(true)
                noDataFound.clNoDataFound.visible(false)

                shippingAddressList.addAll(addressListResponse.data)
                shippingAddressAdapter.notifyDataSetChanged()
            } else {
                noDataFound.clNoDataFound.visible(true)
                nestedScrollview.visible(false)
            }
        }
    }

    // ============== GET ADDRESS DATA
    private fun callGetAddressApi() {
        Log.e("GetAddress", "Calling...")
        customProgressDialog.show()
        (activity as HomeActivity).homeViewModel.getAddressList(
            getHeaderMap(
                data.token,
                true
            )
        )
    }

    // ============== Observe Address List Response
    private fun observeGetAddressResponse(binding: FragmentShippingAddressBinding) {
        (activity as HomeActivity).homeViewModel.getAddressListResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { addressList ->
                        setUpAddressListUI(binding, addressList)
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


    // Call Set Primary Address Api
    private fun callSetPrimaryAddressApi(id: Int) {
        (activity as HomeActivity).homeViewModel.setPrimaryAddress(
            getHeaderMap(
                data.token,
                true
            ),
            id = id
        )
    }

    // Observe Primary Address Response
    private fun observePrimaryAddressResponse() {
        (activity as HomeActivity).homeViewModel.setPrimaryAddressResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { addressResponse ->
                        if (addressResponse.data.size > 0) {
                            if (addressResponse.data[0] == 1) {
                                callGetAddressApi()
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

    override fun onItemClick(data: GetAddressListData, position: Int) {
        (activity as HomeActivity).showShortToast("You click ${data.addressType}")
        callSetPrimaryAddressApi(data.id)
        observePrimaryAddressResponse()
    }
}