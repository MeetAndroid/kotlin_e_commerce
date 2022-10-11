package com.specindia.ecommerce.ui.productdetails

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
import com.google.gson.reflect.TypeToken
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentProductDetailsBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.request.AddUpdateCartWithCartId
import com.specindia.ecommerce.models.request.AddUpdateCartWithProductId
import com.specindia.ecommerce.models.request.RemoveFromCartParam
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.cart.getcart.GetCartData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.util.*
import com.specindia.ecommerce.util.Constants.Companion.IS_FROM_PRODUCT_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.REQUEST_FROM_RESTAURANT_DETAILS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private val args: ProductDetailsFragmentArgs by navArgs()
    var productId = 0
    var tags = ""
    var bundle = ""
    private lateinit var productsByRestaurantData: ProductsByRestaurantData
    private lateinit var recentData: ProductsByRestaurantData
    var existingRestaurantIdInCart = 0
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog
    private var getCartDataBasedOnProductId = ArrayList<GetCartData>()
    private var cartList = ArrayList<GetCartData>()
    private var cartData: GetCartData? = null
    var isFirst: Boolean = false
    var isRemove: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProgressDialog()
        setUpHeader()
        setUpData()
        setUpHeaderItemClick()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
        binding.clTopPart.setRandomBackgroundColor()
        callGetCartApi()
        Handler(Looper.getMainLooper()).postDelayed({
            observeGetCartResponse()
            observerAddButton()
            observeRemoveFromCartResponse()
        }, 2000)
        customProgressDialog.show()

    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
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

    private fun setUpData() {
        productId = args.productId
        tags = args.tag
        bundle = args.bundle
        val recentProduct = object : TypeToken<ProductsByRestaurantData>() {}.type
        productsByRestaurantData = Gson().fromJson(bundle, recentProduct)
        existingRestaurantIdInCart =
            (activity as HomeActivity).dataStoreViewModel.getExistingRestaurantIdFromCart()!!
        setRecentItem(activity as HomeActivity, productsByRestaurantData)

        /* if (existingRestaurantIdInCart == 0) {
             binding.clAddOrRemoveProduct.isVisible = false
             binding.btn1.isVisible = true
         } else {
             binding.clAddOrRemoveProduct.isVisible = true
             binding.btn1.isVisible = false
             binding.tvQty.text = productsByRestaurantData.totalQty.toString()
         }
 */
        with(binding) {
            Glide.with(requireActivity()).load(productsByRestaurantData.productImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.ic_dialog_alert).into(ivRestaurant)
            tvTitle.text = productsByRestaurantData.productName
            tvPrice.text =
                getString(
                    R.string.currency_amount,
                    productsByRestaurantData.price.toString()
                )
            tvContent.text = productsByRestaurantData.description

            tvCalories.text = "147"
            tvCarbs.text = "127g"
            tvFat.text = "2.9g"
            tvProtein.text = "17g"
        }

        binding.btn1.setOnClickListener {
            if (!requireActivity().isConnected) {
                showMaterialSnack(
                    requireContext(), it, getString(R.string.message_no_internet_connection)
                )
            } else {
                if (existingRestaurantIdInCart == 0) {
                    customProgressDialog.show()
                    callAddButtonApiCall()
                } else {
                    if (existingRestaurantIdInCart == productsByRestaurantData.restaurantId) {
                        customProgressDialog.show()
                        callAddButtonApiCall()
                    } else {
                        clearItemsFromCartAndAddTheNewOne()
                    }
                }
            }
        }

        binding.btnAddProduct.setOnClickListener {
//            customProgressDialog.show()
            val dataCart =
                cartList.filter { it.productId == productsByRestaurantData.productId }
            cartData = dataCart.first()
            var qty = cartData?.quantity?.toInt()
            qty = qty?.plus(1)
            val parameter = AddUpdateCartWithCartId(
                id = cartData?.id.toString(),
                quantity = qty.toString(),
                amount = cartData?.product?.price.toString()
            )
            (activity as HomeActivity).homeViewModel.addUpdateToCart(getHeaderMap(data.token, true), Gson().toJson(parameter))
        }

        binding.btnRemoveProduct.setOnClickListener {
//            customProgressDialog.show()
            val dataCart =
                cartList.filter { it.productId == productsByRestaurantData.productId }
            cartData = dataCart.first()
            val qty = cartData?.quantity?.toInt()?.minus(1)
            if (cartData?.quantity == "1") {
                onRemoveProductButtonClick(cartList[0])
            } else {
                val parameter = AddUpdateCartWithCartId(
                    id = cartData?.id.toString(),
                    quantity = qty.toString(),
                    amount = cartData?.product?.price.toString()
                )
                (activity as HomeActivity).homeViewModel.addUpdateToCart(getHeaderMap(data.token, true), Gson().toJson(parameter))
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

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(getString(R.string.app_name))
            .setMessage(message).setPositiveButton(getString(R.string.ok)) { _, _ ->
            }.show()
    }

    /**
    1. Remove All Cart by sending cartId as 0
    2. Then Add current Item to cart
    3. This will clear all items of Previous Restaurant from cart and add the new one for current Restaurant
     * */


    private fun clearItemsFromCartAndAddTheNewOne() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.app_name))
            .setCancelable(false).setMessage(getString(R.string.msg_confirm_change_cart_item))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                isFirst = true
                customProgressDialog.show()
                callRemoveFromCartApi()
                // This will save current Restaurant Id as 0. As we remove all items from Cart

            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }.show()
    }

    private fun callRemoveFromCartApi() {
        val parameter = RemoveFromCartParam(cartId = 0)
        (activity as HomeActivity).homeViewModel.removeFromCart(
            getHeaderMap(
                data.token, true
            ), Gson().toJson(parameter)
        )
    }

    private fun observeRemoveFromCartResponse() {
        (activity as HomeActivity).homeViewModel.removeFromCart.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    if (isFirst) {
                        callAddButtonApiCall()
                    } else if (isRemove) {
                        callGetCartApi()
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

    private fun callAddButtonApiCall() {
        val parameter = AddUpdateCartWithProductId(
            productId = productsByRestaurantData.productId.toString(),
            quantity = "1",
            amount = productsByRestaurantData.price.toString()
        )
        (activity as HomeActivity).homeViewModel.addUpdateToCart(
            getHeaderMap(data.token, true), Gson().toJson(parameter)
        )
    }

    private fun observerAddButton() {
        (activity as HomeActivity).homeViewModel.addUpdateToCart.observe(viewLifecycleOwner) { res ->
            when (res) {
                is NetworkResult.Success -> {
                    (activity as HomeActivity).dataStoreViewModel.saveExistingRestaurantIdOfCart(productsByRestaurantData.restaurantId)
                    callGetCartApi()
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(res.message.toString())
                }
                is NetworkResult.Loading -> {
//                    customProgressDialog.show()
                }
            }
        }
    }

    // ============== GET CART DATA
    private fun callGetCartApi() {
        Log.e("GetCart", "Calling...")
        (activity as HomeActivity).homeViewModel.getCart(getHeaderMap(data.token, true))
    }

    // ============== Observe Cart Response
    @SuppressLint("UnsafeOptInUsageError")
    private fun observeGetCartResponse() {
        (activity as HomeActivity).homeViewModel.getCartResponse.observe(viewLifecycleOwner) { response ->
            customProgressDialog.dismiss()
            when (response) {
                is NetworkResult.Success -> {
                    cartList.clear()
                    if (response.data != null) {
                        cartList = response.data.data
                        Log.e("CART-LIST", "DATA-DATA:-" + Gson().toJson(cartList))
                    }
                    customProgressDialog.dismiss()
                    (activity as HomeActivity).dataStoreViewModel.saveExistingRestaurantIdOfCart(
                        productsByRestaurantData.restaurantId
                    )

                    response.data?.let {
                        handleCartBadgeCount(it, (activity as HomeActivity), binding.homeDetailsScreenHeader.frShoppingCart)
                    }
                    val showView = cartList.filter { it.productId == productId }
                    for (i in showView.indices) {
                        binding.tvQty.text = showView[i].quantity
                    }
                    if (showView.isNotEmpty()) {
                        binding.clAddOrRemoveProduct.isVisible = true
                        binding.btn1.isVisible = false

                    } else {
                        binding.clAddOrRemoveProduct.isVisible = false
                        binding.btn1.isVisible = true
                    }
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
//                    customProgressDialog.show()
                }
            }
        }
    }


    private fun onRemoveProductButtonClick(cart: GetCartData) {

        if (cart.quantity == "1") {
            isRemove = true
            val parameter = RemoveFromCartParam(
                cartId = cart.id
            )

            (activity as HomeActivity).homeViewModel.removeFromCart(
                getHeaderMap(
                    data.token,
                    true
                ),
                Gson().toJson(parameter)
            )

        } else {
            var qty = cart.quantity.toInt()
            qty -= 1
            val parameter = AddUpdateCartWithCartId(
                id = cart.id.toString(),
                quantity = qty.toString(),
                amount = cart.amount
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
}