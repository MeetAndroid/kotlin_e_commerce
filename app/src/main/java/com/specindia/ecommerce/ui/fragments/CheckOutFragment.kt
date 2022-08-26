package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentCheckoutBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.showProgressDialog
import com.specindia.ecommerce.util.showShortToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckOutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val args: CheckOutFragmentArgs by navArgs()
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private var total: Int = 0
    private var subTotal: Int = 0
    private var deliveryCost: Int = 0
    private var restaurantId: Int = 0
    private var addressId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCheckoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        total = args.total
        subTotal = args.subTotal
        deliveryCost = args.extraCharges
        restaurantId = args.restaurantId

        setUpHeader()
        setUpHeaderItemClick()
        setUpButtonClick()
        setUpData()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        // (activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()
    }

    private fun setUpData() {
        val primaryAddressInfo =
            (activity as HomeActivity).dataStoreViewModel.getPrimaryAddressInfo()

        val addressType = object : TypeToken<GetAddressListData>() {}.type
        val address = Gson().fromJson<GetAddressListData>(primaryAddressInfo, addressType)

        var fullAddress = ""
        if (address == null) {
            binding.tvChange.text = getString(R.string.add)
        } else {
            fullAddress = address.firstLine.plus(address.secondLine).plus(address.thirdLine)
            binding.tvChange.text = getString(R.string.change)
            binding.tvAddress.text = fullAddress
            addressId = address.id
        }


        binding.apply {
            tvSubTotal.text = getString(R.string.rs, subTotal.toString())
            tvDeliveryCost.text = getString(R.string.rs, deliveryCost.toString())
            tvDiscount.text = "- Rs. 4"
            tvTotal.text = getString(R.string.rs, total.toString())
        }
    }


    private fun setUpHeader() {
        with(binding) {
            with(orderDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.check_out)
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


    private fun setUpButtonClick() {
        binding.btnSendOrder.setOnClickListener {

            if (addressId != 0) {
                callCreateOrderApi()
                observeCreateOrderResponse(it)
            } else {
                (activity as HomeActivity).showShortToast(getString(R.string.add_primary_address))
            }

        }
        binding.tvChange.setOnClickListener {
            it.findNavController()
                .navigate(CheckOutFragmentDirections.actionCheckOutFragmentToShippingAddressFragment())
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    // ============== GET CART DATA
    private fun callCreateOrderApi() {
        customProgressDialog.show()
        val parameter = Parameters(
            addressId = addressId.toString(),
            extraCharges = deliveryCost.toString(),
            restaurantId = restaurantId,
            subtotal = subTotal.toString(),
            total = total.toString()
        )

        (activity as HomeActivity).homeViewModel.createOrder(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )
    }


    private fun observeCreateOrderResponse(view: View) {
        (activity as HomeActivity).homeViewModel.createOrderResponse.observe(viewLifecycleOwner) { response ->


            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        showDialog(getString(R.string.msg_order_created), true, view)
                    }
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(response.message.toString(), false, view)
                }
                is NetworkResult.Loading -> {
                    customProgressDialog.show()
                }
            }
        }
    }

    private fun showDialog(message: String, isGoBack: Boolean, view: View) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                if (isGoBack) {
                    view.findNavController().popBackStack()
                }
            }
            .show()
    }
}