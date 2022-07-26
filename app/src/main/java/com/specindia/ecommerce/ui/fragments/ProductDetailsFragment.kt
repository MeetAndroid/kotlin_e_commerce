package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentProductDetailsBinding
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.setRandomBackgroundColor
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var productsByRestaurantData: ProductsByRestaurantData
    private val args: ProductDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
        binding.clTopPart.setRandomBackgroundColor()
        setUpData()
    }

    private fun setUpData() {
        val productId = args.productId
        val productList =
            (activity as HomeActivity).homeViewModel.productsByRestaurant.value?.data?.data
        val data = productList?.filter { it.id == productId }
        productsByRestaurantData = data!!.first()
        Log.d("Product Data", Gson().toJson(productsByRestaurantData))
        with(binding) {
            Glide.with(requireActivity())
                .load(productsByRestaurantData.productImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.ic_dialog_alert)
                .into(ivRestaurant)
            tvTitle.text = productsByRestaurantData.productName
            tvPrice.text =
                getString(R.string.currency_amount, productsByRestaurantData.price.toString())
            tvContent.text = productsByRestaurantData.description
            tvCalories.text = "147"
            tvCarbs.text = "127g"
            tvFat.text = "2.9g"
            tvProtein.text = "17g"

        }
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

}