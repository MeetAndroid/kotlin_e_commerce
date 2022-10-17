package com.specindia.ecommerce.ui.checkout.address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentShippingAddressBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListData
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListResponse
import com.specindia.ecommerce.ui.checkout.address.adapter.ShippingAddressAdapter
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
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

        // call get address list api
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
            //redirect to setLocationFragment
            btnAddNewAddress.setOnClickListener {
                if (!requireActivity().isConnected) {
                    showMaterialSnack(
                        requireContext(),
                        it,
                        getString(R.string.message_no_internet_connection)
                    )

                } else {
                    it.findNavController()
                        .navigate(ShippingAddressFragmentDirections.actionShippingAddressFragmentToSetLocationFragment())
                }

            }
            noDataFound.tvNoData.setOnClickListener {
                if (!requireActivity().isConnected) {
                    showMaterialSnack(
                        requireContext(),
                        it,
                        getString(R.string.message_no_internet_connection)
                    )

                } else {
                    it.findNavController()
                        .navigate(ShippingAddressFragmentDirections.actionShippingAddressFragmentToSetLocationFragment())
                }

            }

        }
    }

    // set data recyclerview
    private fun setUpRecyclerView() {
        // Addresses
        shippingAddressList = ArrayList()
        shippingAddressAdapter =
            ShippingAddressAdapter(shippingAddressList, this, (activity as HomeActivity))
        binding.rvShippingAddress.apply {
            adapter = shippingAddressAdapter
            setHasFixedSize(false)
            addItemDecoration(
                MarginDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_margin_16),
                    false
                )
            )
            isNestedScrollingEnabled = false
            layoutManager =
                LinearLayoutManager(requireActivity())
        }
    }

    // Progress dialog initialize
    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    //show message dialog when response any throw error
    private fun showDialog(message: String, isFromSwipeToDelete: Boolean) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                if (isFromSwipeToDelete) {
                    shippingAddressAdapter.notifyDataSetChanged()
                }
            }
            .show()
    }

    // show address list recyclerview
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

                // Save Primary Address Info in Data Store to show on Checkout default Address

                val primaryAddresses = addressListResponse.data.filter { it.primary }
                if (primaryAddresses.isNotEmpty()) {
                    (activity as HomeActivity).dataStoreViewModel.savePrimaryAddressInfo(
                        Gson().toJson(primaryAddresses[0])
                    )
                }

                shippingAddressList.addAll(addressListResponse.data)
                shippingAddressAdapter.notifyDataSetChanged()

                // Swipe to Delete functionality

                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder,
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val deletedAddress: GetAddressListData =
                            shippingAddressList[viewHolder.adapterPosition]
                        val position = viewHolder.adapterPosition

                        val fullAddress = if (deletedAddress.thirdLine.isEmpty()) {
                            deletedAddress.firstLine.plus(",").plus(deletedAddress.secondLine)
                        } else if (deletedAddress.secondLine.isEmpty()) {
                            deletedAddress.firstLine.plus(",")
                        } else {
                            deletedAddress.firstLine.plus(",").plus(deletedAddress.secondLine)
                                .plus(",")
                                .plus(deletedAddress.thirdLine)
                        }


                        if (deletedAddress.primary) {
                            showDialog(getString(R.string.msg_can_not_delete_primary_address), true)

                        } else {
                            confirmToDeleteAddress(
                                (activity as HomeActivity),
                                deletedAddress,
                                fullAddress,
                                position,
                                viewHolder.itemView
                            )
                        }

                    }
                }).attachToRecyclerView(rvShippingAddress)

            } else {
                noDataFound.clNoDataFound.visible(true)
                nestedScrollview.visible(false)
                noDataFound.tvNoData.text = getString(R.string.no_address_found_add_new_address)
            }
        }
    }

    // delete to save address but when your address is primary not delete
    fun confirmToDeleteAddress(
        activity: HomeActivity,
        deletedAddress: GetAddressListData,
        fullAddress: String,
        position: Int,
        itemView: View,
    ) {
        MaterialAlertDialogBuilder(activity)
            .setCancelable(false)
            .setTitle(activity.getString(R.string.app_name))
            .setMessage(activity.getString(R.string.msg_confirm_delete_address))
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                if (!requireActivity().isConnected) {
                    showMaterialSnack(
                        requireContext(),
                        itemView,
                        getString(R.string.message_no_internet_connection)
                    )
                    shippingAddressAdapter.notifyDataSetChanged()
                } else {
                    callRemoveAddressApi(deletedAddress.id)
                    observeRemoveAddressResponse(fullAddress)
                }

            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                shippingAddressAdapter.notifyDataSetChanged()
            }
            .show()
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
                    showDialog(response.message.toString(), false)
                }
                is NetworkResult.Loading -> {
                    customProgressDialog.show()
                }
            }
        }
    }


    // Call Set Primary Address Api
    private fun callSetPrimaryAddressApi(id: Int) {

        val parameter = Parameters(
            id = id.toString()
        )
        (activity as HomeActivity).homeViewModel.setPrimaryAddress(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )
    }

    // Observe Primary Address Response
    private fun observePrimaryAddressResponse(data: GetAddressListData) {
        (activity as HomeActivity).homeViewModel.setPrimaryAddressResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { addressResponse ->
                        if (addressResponse.data.toString().contains("1")) {
                            (activity as HomeActivity).showShortToast(getString(R.string.primary_address_changed))
                            callGetAddressApi()
                        }
                    }
                }
                is NetworkResult.Error -> {
                    showDialog(response.message.toString(), false)
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    // call to remove address api
    private fun callRemoveAddressApi(id: Int) {
        val parameter = Parameters(id = id.toString())
        (activity as HomeActivity).homeViewModel.removeAddress(getHeaderMap(data.token, true), Gson().toJson(parameter))
    }

    // Observe Remove Address Response
    private fun observeRemoveAddressResponse(fullAddress: String) {
        (activity as HomeActivity).homeViewModel.removeAddressResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { addressResponse ->
                        if (addressResponse.data.toString().contains("1")) {
                            (activity as HomeActivity).showShortToast("Deleted $fullAddress")
                            callGetAddressApi()
                        }
                    }
                }
                is NetworkResult.Error -> {
                    showDialog(response.message.toString(), false)
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    // set to primary address when user click address
    override fun onItemClick(data: GetAddressListData, position: Int) {
        callSetPrimaryAddressApi(data.id)
        observePrimaryAddressResponse(data)
    }
}