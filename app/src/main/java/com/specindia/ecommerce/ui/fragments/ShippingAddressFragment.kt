package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentShippingAddressBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.shippingaddress.ShippingAddressData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.ShippingAddressAdapter
import com.specindia.ecommerce.util.MarginDecoration
import com.specindia.ecommerce.util.showProgressDialog
import com.specindia.ecommerce.util.showShortToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAddressFragment : Fragment(), ShippingAddressAdapter.OnShippingAddressClickListener {

    private lateinit var binding: FragmentShippingAddressBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var shippingAddressAdapter: ShippingAddressAdapter
    private lateinit var shippingAddressList: ArrayList<ShippingAddressData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShippingAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeader()
        setUpHeaderItemClick()
        setUpButtonClick()
        setUpRecyclerView()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
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
        // Carts
        shippingAddressList = ArrayList()
        val address1 = ShippingAddressData("Home",
            "Sun Divine 5, Janta Nagar Kakoldiya Rd, Opposite Vrajdham 1 Apartments, Janta Nagar, Ghatlodiya, Chanakyapuri, Ahmedabad, Gujarat 380061")
        val address2 = ShippingAddressData("Work",
            "Spec India, Parth Complex, SPEC House, Swastik Society, Navrangpura, Ahmedabad, Gujarat 380009")
        shippingAddressList.add(address1)
        shippingAddressList.add(address2)

        shippingAddressAdapter = ShippingAddressAdapter(shippingAddressList, this)
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


    override fun onItemClick(data: ShippingAddressData, position: Int) {
        (activity as HomeActivity).showShortToast("You click ${data.type}")
    }
}