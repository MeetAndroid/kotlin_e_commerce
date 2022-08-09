package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentProductDetailsBinding
import com.specindia.ecommerce.models.response.home.SearchItem
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.Constants.Companion.IS_FROM_PRODUCT_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.REQUEST_FROM_RESTAURANT_DETAILS
import com.specindia.ecommerce.util.setRandomBackgroundColor
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var productsByRestaurantData: ProductsByRestaurantData
    private lateinit var searchItem: SearchItem
    private val args: ProductDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        binding.clTopPart.setOnClickListener {
            view.findNavController()
                .navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToOrderDetailsFragment())
        }
    }

    private fun setUpData() {
        val productId = args.productId
        val tag = args.tag
        if (tag == Constants.RESTAURANT) {
            val productList =
                (activity as HomeActivity).homeViewModel.productsByRestaurant.value?.data?.data
            val data = productList?.filter { it.id == productId }
            productsByRestaurantData = data!!.first()
        } else {
            val productList =
                (activity as HomeActivity).homeViewModel.searchResponse.value?.data?.data
            val data = productList?.filter { it.id == productId }
            searchItem = data!!.first()
        }

        with(binding) {
            if (tag == Constants.RESTAURANT) {
                Glide.with(requireActivity())
                    .load(productsByRestaurantData.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivRestaurant)
                tvTitle.text = productsByRestaurantData.productName
                tvPrice.text =
                    getString(R.string.currency_amount, productsByRestaurantData.price.toString())
                tvContent.text = productsByRestaurantData.description
            } else {
                Glide.with(requireActivity())
                    .load(searchItem.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivRestaurant)
                tvTitle.text = searchItem.productName
                tvPrice.text =
                    getString(R.string.currency_amount, searchItem.price.toString())
                tvContent.text = searchItem.description
            }

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
                tvHeaderTitle.text = getString(R.string.product_details)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                ivShoppingCart.visible(true)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    setFragmentResult(
                        REQUEST_FROM_RESTAURANT_DETAILS,
                        bundleOf(IS_FROM_PRODUCT_DETAILS to true)
                    )
                    it.findNavController().popBackStack()
                }
                ivShoppingCart.setOnClickListener {
                    view?.findNavController()
                        ?.navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToCartListFragment())

                }
            }
        }
    }
}