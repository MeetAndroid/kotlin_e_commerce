package com.specindia.ecommerce.ui.fragments

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
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentAddAddressBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.AddAddressAdapter
import com.specindia.ecommerce.util.MarginDecoration
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.showProgressDialog
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog
    private var addressLines = ArrayList<String>()
    private lateinit var addAddressAdapter: AddAddressAdapter
    private var fullAddress: String = ""

    private var addressType = "1"
    private var firstLine = ""
    private var secondLine = ""
    private var thirdLine = ""
    private var latitude: String = "0.0"
    private var longitude: String = "0.0"

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
        Log.d("AddAddress", "onViewCreated")
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        fullAddress = args.fullAddress
        latitude = args.latitude
        longitude = args.longitude

        setUpHeader()
        setUpHeaderItemClick()
        setUpButtonClick()
        setUpRecyclerView()
        setUpProgressDialog()

    }

    private fun setUpHeader() {
        with(binding) {
            with(shippingAddressScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.add_address)
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
                callAddOrUpdateAddressApi()
                observeAddOrUpdateAddressResponse(it)
            }
        }
    }

    private fun setUpRecyclerView() {

        if (fullAddress.isNotEmpty()) {
            binding.noDataFound.clNoDataFound.visible(false)
            binding.nestedScrollview.visible(true)
            if (fullAddress.contains(",")) {
                addressLines = fullAddress.split(",") as ArrayList<String>
            } else {
                addressLines.add(fullAddress)
            }
            addAddressAdapter = AddAddressAdapter(addressLines, requireContext())
            binding.rvShippingAddress.apply {
                adapter = addAddressAdapter
                setHasFixedSize(false)
                addItemDecoration(MarginDecoration(resources.getDimensionPixelSize(R.dimen.item_margin_16),
                    false))
                isNestedScrollingEnabled = false
                layoutManager =
                    LinearLayoutManager(requireActivity())
            }
        } else {
            binding.noDataFound.clNoDataFound.visible(true)
            binding.nestedScrollview.visible(false)
        }

    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    // ============== GET CART DATA
    private fun callAddOrUpdateAddressApi() {
        addressType = binding.spAddressType.selectedItem.toString()
        convertAddressListToLines()

        customProgressDialog.show()
        val parameter = Parameters(
            addressType = addressType,
            firstLine = firstLine,
            secondLine = secondLine,
            thirdLine = thirdLine,
            lat = latitude,
            lang = longitude
        )

        (activity as HomeActivity).homeViewModel.addOrUpdateAddress(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )
    }

    private fun convertAddressListToLines() {
        try {
            if (addressLines.size <= 0) {
                return
            } else if (addressLines.size <= 3) {
                when (addressLines.size) {
                    3 -> {
                        firstLine = addressLines[0]
                        secondLine = addressLines[1]
                        thirdLine = addressLines[2]
                    }
                    2 -> {
                        firstLine = addressLines[0]
                        secondLine = addressLines[1]
                        thirdLine = ""
                    }
                    else -> {
                        firstLine = addressLines[0]
                        secondLine = ""
                        thirdLine = ""
                    }
                }
            } else if (addressLines.size == 4) {
                firstLine = addressLines[0].plus(addressLines[1])
                secondLine = addressLines[2]
                thirdLine = addressLines[3]
            } else {
                var myChunkSize = 0
                val rem = addressLines.size % 3
                myChunkSize = when (rem) {
                    0 -> addressLines.size / 3
                    else ->
                        (addressLines.size / 3) + 1
                }
                Log.d("myChunkSize", myChunkSize.toString())

                val chunkList = addressLines.chunked(myChunkSize)

                firstLine = if (chunkList[0].isNotEmpty()) {
                    chunkList[0].toString().removeSurrounding("[", "]")
                } else {
                    ""
                }
                secondLine = if (chunkList[1].isNotEmpty()) {
                    chunkList[1].toString().removeSurrounding("[", "]")
                } else {
                    ""
                }
                thirdLine = if (chunkList[2].isNotEmpty()) {
                    chunkList[2].toString().removeSurrounding("[", "]")
                } else {
                    ""
                }

            }
            Log.d("firstLine ", firstLine)
            Log.d("secondLine ", secondLine)
            Log.d("thirdLine ", thirdLine)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun observeAddOrUpdateAddressResponse(view: View) {
        (activity as HomeActivity).homeViewModel.addOrUpdateAddressResponse.observe(
            viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {

                        if (response.data.status == "success") {
                            showDialog(getString(R.string.msg_address_added_successesfully),
                                true,
                                view)
                        } else {
                            showDialog(response.data.message, false, view)
                        }

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
                    view.findNavController()
                        .navigate(AddAddressFragmentDirections.actionAddAddressFragmentToShippingAddressFragment())
                }
            }
            .show()
    }
}