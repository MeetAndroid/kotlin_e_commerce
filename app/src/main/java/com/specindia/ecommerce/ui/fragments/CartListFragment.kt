package com.specindia.ecommerce.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentCartListBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.cart.getcart.GetCartData
import com.specindia.ecommerce.models.response.cart.getcart.GetCartResponse
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.CartListAdapter
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.showProgressDialog
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartListFragment : Fragment(), CartListAdapter.OnCartItemClickListener {

    private lateinit var binding: FragmentCartListBinding
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var cartListAdapter: CartListAdapter
    private lateinit var cartList: ArrayList<GetCartData>

    private var restaurantId: Int = 0
//    private var productRemovedFromCart: ProductsByRestaurantData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartListBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
        setUpRecyclerView()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)


//        observeAddUpdateCartResponse()
//        observeRemoveFromCartResponse()
        callGetCartApi()
        observeGetCartResponse()

        (activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callGetCartApi()
        }

    }

    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.my_cart)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivSearch.visible(false)
                ivShoppingCart.visible(false)
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


    private fun setUpRecyclerView() {
        // Carts
        cartList = ArrayList()
        cartListAdapter = CartListAdapter(cartList, this)
        binding.rvCart.apply {
            adapter = cartListAdapter
            setHasFixedSize(false)
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

    private fun setUpCartListUI(
        binding: FragmentCartListBinding,
        cartListResponse: GetCartResponse,
    ) {
        binding.apply {
            cartList.clear()
            noDataFound.clNoDataFound.visible(true)
            rvCart.visible(false)
            if (cartListResponse.data.isNotEmpty()) {
                rvCart.visible(true)
                noDataFound.clNoDataFound.visible(false)
                cartList.addAll(cartListResponse.data.toList())
                cartListAdapter.notifyDataSetChanged()
            } else {
                noDataFound.clNoDataFound.visible(true)
                rvCart.visible(false)
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
    }

    // ============== Observe Cart Response
    @SuppressLint("LongLogTag")
    private fun observeGetCartResponse() {
        (activity as HomeActivity).homeViewModel.getCartResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.hide()
                    response.data?.let { cartListResponse ->
                        setUpCartListUI(binding,cartListResponse)
                    }
                }
                is NetworkResult.Error -> {
                    customProgressDialog.hide()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                    customProgressDialog.show()
                }
            }
        }
    }

    // ================ ADD OR REMOVE PRODUCT FROM CART

//    private fun callAddUpdateToCartApi(
//        productOrCartId: String,
//        qty: String,
//        amount: String,
//        isCartExist: Boolean,
//    ) {
//        customProgressDialog.show()
//        if (isCartExist) {
//            val parameter = AddUpdateCartWithCartId(
//                id = productOrCartId,
//                quantity = qty,
//                amount = amount
//            )
//
//            (activity as HomeActivity).homeViewModel.addUpdateToCart(
//                getHeaderMap(
//                    data.token,
//                    true
//                ),
//                Gson().toJson(parameter)
//            )
//        } else {
//            val parameter = AddUpdateCartWithProductId(
//                productId = productOrCartId,
//                quantity = qty,
//                amount = amount
//            )
//
//            (activity as HomeActivity).homeViewModel.addUpdateToCart(
//                getHeaderMap(
//                    data.token,
//                    true
//                ),
//                Gson().toJson(parameter)
//            )
//        }
//
//    }


    // ============== Observe Products By Restaurant Response
//    private fun observeAddUpdateCartResponse() {
//        (activity as HomeActivity).homeViewModel.addUpdateToCart.observe(
//            viewLifecycleOwner
//        ) { response ->
//
//            when (response) {
//                is NetworkResult.Success -> {
//                    customProgressDialog.hide()
//                    response.data?.let {
//                        callGetCartApi()
//                    }
//                }
//                is NetworkResult.Error -> {
//                    customProgressDialog.hide()
//                    showDialog(response.message.toString())
//                }
//                is NetworkResult.Loading -> {
//                    customProgressDialog.show()
//                }
//            }
//        }
//    }

//    override fun onItemClick(data: ProductsByRestaurantData, position: Int) {
//        view?.findNavController()
//            ?.navigate(
//                RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToProductDetailsFragment(
//                    data.id, Constants.RESTAURANT
//                )
//            )
//    }

    // + Button click
//    override fun onAddProductButtonClick(
//        data: ProductsByRestaurantData,
//        position: Int,
//    ) {
//        data.totalQty = data.totalQty + 1
//
//        callAddUpdateToCartApi(
//            productOrCartId = data.cartId.toString(),
//            qty = data.totalQty.toString(),
//            amount = data.price.toString(),
//            isCartExist = data.isCartExist
//        )
//
//
//    }

    // - Button Click
//    override fun onRemoveProductButtonClick(
//        data: ProductsByRestaurantData,
//        position: Int,
//    ) {
//        data.totalQty = data.totalQty - 1
//        if (data.totalQty == 0) {
//            // Call Remove Cart API
//            callRemoveFromCartApi(data.cartId)
//            productRemovedFromCart = data
//
//        } else {
//            callAddUpdateToCartApi(
//                productOrCartId = data.cartId.toString(),
//                qty = data.totalQty.toString(),
//                amount = data.price.toString(),
//                isCartExist = data.isCartExist
//            )
//
//        }
//
//    }

//    private fun callRemoveFromCartApi(cartId: Int) {
//        customProgressDialog.show()
//
//        val parameter = RemoveFromCartParam(
//            cartId = cartId
//        )
//
//        (activity as HomeActivity).homeViewModel.removeFromCart(
//            getHeaderMap(
//                data.token,
//                true
//            ),
//            Gson().toJson(parameter)
//        )
//
//    }

    // Add Button Click
//    override fun onAddButtonClick(
//        data: ProductsByRestaurantData,
//        position: Int,
//    ) {
//        data.totalQty = 1
//        if (data.isCartExist) {
//            callAddUpdateToCartApi(
//                productOrCartId = data.cartId.toString(),
//                qty = data.totalQty.toString(),
//                amount = data.price.toString(),
//                isCartExist = true
//            )
//        } else {
//            callAddUpdateToCartApi(
//                productOrCartId = data.id.toString(),
//                qty = data.totalQty.toString(),
//                amount = data.price.toString(),
//                isCartExist = false
//            )
//        }
//
//    }


//    private fun observeRemoveFromCartResponse() {
//        (activity as HomeActivity).homeViewModel.removeFromCart.observe(
//            viewLifecycleOwner
//        ) { response ->
//
//            when (response) {
//                is NetworkResult.Success -> {
//                    customProgressDialog.hide()
//                    response.data?.let {
//                        if (it.data == 1) {
//                            val productsByRestaurant =
//                                (activity as HomeActivity).homeViewModel.productsByRestaurant.value
//                            if (productsByRestaurant != null) {
//                                if (productsByRestaurant.data != null) {
//                                    val productListResponse = productsByRestaurant.data
//                                    if (productListResponse.data.size > 0) {
//                                        for (i in 0 until productListResponse.data.size) {
//                                            if (productRemovedFromCart != null) {
//                                                if (productListResponse.data[i].id == productRemovedFromCart?.id) {
//                                                    productListResponse.data[i].totalQty = 0
//                                                    productListResponse.data[i].isCartExist = false
//                                                    callGetCartApi()
//                                                    break
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                is NetworkResult.Error -> {
//                    customProgressDialog.hide()
//                    showDialog(response.message.toString())
//                }
//                is NetworkResult.Loading -> {
//                    customProgressDialog.show()
//                }
//            }
//        }
//    }

    override fun onItemClick(data: GetCartData, position: Int) {

    }

    override fun onAddButtonClick(data: GetCartData, position: Int) {

    }

    override fun onAddProductButtonClick(data: GetCartData, position: Int) {

    }

    override fun onRemoveProductButtonClick(data: GetCartData, position: Int) {

    }
}