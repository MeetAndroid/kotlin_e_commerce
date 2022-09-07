package com.specindia.ecommerce.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentProductDetailsBinding
import com.specindia.ecommerce.models.request.AddUpdateCartWithCartId
import com.specindia.ecommerce.models.request.AddUpdateCartWithProductId
import com.specindia.ecommerce.models.request.RemoveFromCartParam
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.cart.getcart.GetCartData
import com.specindia.ecommerce.models.response.home.SearchItem
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.*
import com.specindia.ecommerce.util.Constants.Companion.IS_FROM_PRODUCT_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.REQUEST_FROM_RESTAURANT_DETAILS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var productsByRestaurantData: ProductsByRestaurantData
    private var searchItem: SearchItem? = null
    private var cartData: GetCartData? = null
    private val args: ProductDetailsFragmentArgs by navArgs()
    var existingRestaurantIdInCart = 0
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog
    private var getCartDataBasedOnProductId = ArrayList<GetCartData>()
    private var cartList = ArrayList<GetCartData>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
        setUpHeader()
        setUpHeaderItemClick()
        binding.clTopPart.setRandomBackgroundColor()
        setUpData()
        callGetCartApi()
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    private fun setUpData() {
        val productId = args.productId
        Log.e("PRODUCT-ID", productId.toString())
        val tag = args.tag
        if (tag == Constants.RESTAURANT) {
            val productList =
                (activity as HomeActivity).homeViewModel.productsByRestaurant.value?.data?.data
            existingRestaurantIdInCart =
                (activity as HomeActivity).dataStoreViewModel.getExistingRestaurantIdFromCart()!!
            val data = productList?.filter { it.id == productId }
            productsByRestaurantData = data!!.first()
            Log.e("PRODUCT-DATA", Gson().toJson(productsByRestaurantData))
            Log.e("RESTAURANT-ID", existingRestaurantIdInCart.toString())
            if (productsByRestaurantData.totalQty == 0) {
                binding.clAddOrRemoveProduct.isVisible = false
                binding.btn1.isVisible = true
            } else {
                binding.clAddOrRemoveProduct.isVisible = true
                binding.btn1.isVisible = false
                binding.tvQty.text = productsByRestaurantData.totalQty.toString()
            }
        } else {

            val productList =
                (activity as HomeActivity).homeViewModel.searchResponse.value?.data?.data

            existingRestaurantIdInCart =
                (activity as HomeActivity).dataStoreViewModel.getExistingRestaurantIdFromCart()!!
            val data = productList?.filter { it.id == productId }
            searchItem = data!!.first()
            Log.e("SEARCH", Gson().toJson(searchItem))

            if (searchItem != null) {
                getCartDataBasedOnProductId =
                    (activity as HomeActivity).homeViewModel.getCartResponse.value?.data?.data?.filter {
                        it.product.id == searchItem?.id
                    } as ArrayList<GetCartData>
                Log.e("SEARCH", Gson().toJson(getCartDataBasedOnProductId))
                if (getCartDataBasedOnProductId.isNotEmpty()) {
                    Log.e("ID", getCartDataBasedOnProductId.first().id.toString())
                    Log.e("QTY", getCartDataBasedOnProductId.first().quantity)
                    binding.clAddOrRemoveProduct.isVisible = true
                    binding.btn1.isVisible = false
                    binding.tvQty.text = getCartDataBasedOnProductId.first().quantity
                } else {
                    binding.clAddOrRemoveProduct.isVisible = false
                    binding.btn1.isVisible = true
                }
            }
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
                    .load(searchItem?.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivRestaurant)
                tvTitle.text = searchItem?.productName ?: ""
                tvPrice.text =
                    getString(R.string.currency_amount, searchItem?.price.toString())
                tvContent.text = searchItem?.description ?: ""
            }

            tvCalories.text = "147"
            tvCarbs.text = "127g"
            tvFat.text = "2.9g"
            tvProtein.text = "17g"
        }

        binding.btn1.setOnClickListener {
            if (!requireActivity().isConnected) {
                showMaterialSnack(
                    requireContext(),
                    it,
                    getString(R.string.message_no_internet_connection)
                )
            } else {
                if (tag == Constants.RESTAURANT) {
                    if (existingRestaurantIdInCart == 0) {
                        callAddButtonApiCall()
                        observerAddButton()
                    } else {
                        if (existingRestaurantIdInCart == productsByRestaurantData.restaurantId) {
                            callAddButtonApiCall()
                            observerAddButton()
                        } else {
                            clearItemsFromCartAndAddTheNewOne()
                        }
                    }
                } else {
                    if (existingRestaurantIdInCart != 0) {
                        if (existingRestaurantIdInCart == searchItem?.RestaurantId) {
                            callAddButtonApiCall()
                            observerAddButton()
                        } else {
                            clearItemsFromCartAndAddTheNewOne()
                        }
                    } else {
                        callAddButtonApiCall()
                        observerAddButton()
                    }
                }
            }

        }
        binding.btnAddProduct.setOnClickListener {
            if (tag == Constants.RESTAURANT) {
                /* var qty = data.quantity.toInt()
                 qty += 1
                 val parameter = AddUpdateCartWithCartId(
                     id = data.id.toString(),
                     quantity = qty.toString(),
                     amount = data.amount
                 )

                 (activity as HomeActivity).homeViewModel.addUpdateToCart(
                     getHeaderMap(
                         user.token,
                         true
                     ),
                     Gson().toJson(parameter)
                 )*/
            } else {
                val dataCart = cartList.filter { it.productId == searchItem?.id }
                cartData = dataCart.first()
                if (cartData != null) {
                    customProgressDialog.show()
                    var qty = cartData?.quantity?.toInt()
                    qty = qty?.plus(1)
                    val parameter = AddUpdateCartWithCartId(
                        id = cartData?.id.toString(),
                        quantity = qty.toString(),
                        amount = cartData?.product?.price.toString()
                    )

                    (activity as HomeActivity).homeViewModel.addUpdateToCart(
                        getHeaderMap(
                            data.token,
                            true
                        ),
                        Gson().toJson(parameter)
                    )


                    observeAddUpdateCartResponse()
                }

            }
        }
    }

    @ExperimentalBadgeUtils
    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.product_details)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                frShoppingCart.visible(true)
                updateCartCountOnUI((activity as HomeActivity), frShoppingCart)
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
                frShoppingCart.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )
                    } else {
                        view?.findNavController()
                            ?.navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToCartListFragment())
                    }
                }
            }
        }
    }

    /**
    1. Remove All Cart by sending cartId as 0
    2. Then Add current Item to cart
    3. This will clear all items of Previous Restaurant from cart and add the new one for current Restaurant
     * */

    private fun clearItemsFromCartAndAddTheNewOne() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.app_name))
            .setCancelable(false)
            .setMessage(getString(R.string.msg_confirm_change_cart_item))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                customProgressDialog.show()
                callRemoveFromCartApi()
                Handler(Looper.getMainLooper()).postDelayed({
                    observeRemoveFromCartResponse()
                }, 2000)

                // This will save current Restaurant Id as 0. As we remove all items from Cart

            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
    }

    private fun callRemoveFromCartApi() {

        val parameter = RemoveFromCartParam(cartId = 0)
        (activity as HomeActivity).homeViewModel.removeFromCart(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )
    }

    private fun observeRemoveFromCartResponse() {
        (activity as HomeActivity).homeViewModel.removeFromCart.observe(
            viewLifecycleOwner
        ) { response ->

            view?.findNavController()
                ?.navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToCartListFragment())


            when (response) {
                is NetworkResult.Success -> {
                    callAddButtonApiCall()
                    observerAddButton()
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

    private fun callAddButtonApiCall() {

        if (args.tag == Constants.RESTAURANT) {
            val parameter = AddUpdateCartWithProductId(
                productId = productsByRestaurantData.id.toString(),
                quantity = "1",
                amount = productsByRestaurantData.price.toString()
            )

            (activity as HomeActivity).homeViewModel.addUpdateToCart(
                getHeaderMap(
                    data.token,
                    true
                ),
                Gson().toJson(parameter)
            )
        } else {
            val parameter = AddUpdateCartWithProductId(
                productId = searchItem?.id.toString(),
                quantity = "1",
                amount = searchItem?.price.toString()
            )

            (activity as HomeActivity).homeViewModel.addUpdateToCart(
                getHeaderMap(
                    data.token,
                    true
                ),
                Gson().toJson(parameter)
            )
        }
    }

    private fun observerAddButton() {
        (activity as HomeActivity).homeViewModel.addUpdateToCart.observe(viewLifecycleOwner) { res ->
            when (res) {
                is NetworkResult.Success -> {
                    callGetCartApi()
                    observeGetCartResponse()
                    binding.tvQty.text = res.data?.data?.quantity
                    binding.btn1.isVisible = false
                    binding.clAddOrRemoveProduct.isVisible = true
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(res.message.toString())
                }
                is NetworkResult.Loading -> {
                    customProgressDialog.show()
                }
            }
        }
    }

    // ============== GET CART DATA
    private fun callGetCartApi() {
        Log.e("GetCart", "Calling...")
        customProgressDialog.show()
        (activity as HomeActivity).homeViewModel.getCart(
            getHeaderMap(
                data.token,
                true
            )
        )

        observeGetCartResponse()
    }

    // ============== Observe Cart Response
    @SuppressLint("UnsafeOptInUsageError")
    private fun observeGetCartResponse() {
        (activity as HomeActivity).homeViewModel.getCartResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    cartList.clear()
                    if (response.data != null) {
                        cartList = response.data.data
                    }
                    Log.e("CART-LIST", Gson().toJson(cartList))
                    customProgressDialog.dismiss()
                    if (tag == Constants.RESTAURANT) {
                        (activity as HomeActivity).dataStoreViewModel.saveExistingRestaurantIdOfCart(
                            productsByRestaurantData.restaurantId
                        )
                    } else {
                        searchItem?.RestaurantId?.let {
                            (activity as HomeActivity).dataStoreViewModel.saveExistingRestaurantIdOfCart(
                                it
                            )
                        }
                    }
                    response.data?.let { cartList ->
                        handleCartBadgeCount(
                            cartList, (activity as HomeActivity),
                            binding.homeDetailsScreenHeader.frShoppingCart
                        )
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

    // ============== Observe Products Response
    private fun observeAddUpdateCartResponse() {
        (activity as HomeActivity).homeViewModel.addUpdateToCart.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        callGetCartApi()
                        observeGetCartResponse()
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

}