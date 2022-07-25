package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.specindia.ecommerce.databinding.FragmentRestaurantDetailsBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants.Companion.KEY_RESTAURANT_ID
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.setRandomBackgroundColor
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantDetailsFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantDetailsBinding
    private lateinit var data: AuthResponseData
    private var restaurantId: Int = 0

    // get the arguments from the Registration fragment
//    val args: RestaurantDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantId = arguments?.getInt(KEY_RESTAURANT_ID) ?: 0
        Log.d("restaurantId", restaurantId.toString())
        setUpHeader()
        setUpHeaderItemClick()

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        binding.clTopPart.setRandomBackgroundColor()
        callRestaurantDetailsApi(data)
        observeResponse()
    }


    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = "Restaurant Details"
                ivBack.visible(true)
                ivFavorite.visible(true)
                ivSearch.visible(false)
                ivShoppingCart.visible(true)
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

    private fun callRestaurantDetailsApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getRestaurantDetails(
            getHeaderMap(
                data.token,
                true
            ),
            id = restaurantId
        )
    }

    private fun observeResponse() {
        (activity as HomeActivity).homeViewModel.restaurantDetailsResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { restaurantData ->
                        with(binding) {
                            tvRestaurantName.text = restaurantData.data.name
                            tvRestaurantAddress.text = restaurantData.data.address

                            Glide.with(ivMenuItem)
                                .load(restaurantData.data.imageUrl)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(android.R.drawable.ic_dialog_alert)
                                .into(ivMenuItem)
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

}