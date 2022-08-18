package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentAddAddressBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.AddAddressAdapter
import com.specindia.ecommerce.util.MarginDecoration
import com.specindia.ecommerce.util.showProgressDialog
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var addAddressAdapter: AddAddressAdapter

    private var fullAddress: String = ""

    private val args: AddAddressFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullAddress = args.fullAddress

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
            btnSave.setOnClickListener {
                it.findNavController()
                    .navigate(ShippingAddressFragmentDirections.actionShippingAddressFragmentToSetLocationFragment())
            }
        }
    }


    private fun setUpRecyclerView() {
        // Carts
        val addressLines: ArrayList<String> = fullAddress.split(",") as ArrayList<String>
        addAddressAdapter = AddAddressAdapter(addressLines)
        binding.rvShippingAddress.apply {
            adapter = addAddressAdapter
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

}