package com.specindia.ecommerce.ui.restautentdetaills

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.util.Predicate
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentRestaurantDetailsBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.FavRestaurants
import com.specindia.ecommerce.models.request.AddUpdateCartWithCartId
import com.specindia.ecommerce.models.request.AddUpdateCartWithProductId
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.request.RemoveFromCartParam
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantResponse
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.ui.restautentdetaills.adapter.ProductListAdapter
import com.specindia.ecommerce.util.*
import com.specindia.ecommerce.util.Constants.Companion.IS_FROM_PRODUCT_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.REQUEST_FROM_RESTAURANT_DETAILS
import dagger.hilt.android.AndroidEntryPoint

/**
 * This fragment show Restaurant details and product list
 * Get parameter name is restaurant_id (it's define in nav_home file)
 */

@AndroidEntryPoint
class RestaurantDetailsFragment : Fragment(), ProductListAdapter.OnProductItemClickListener {

    private lateinit var binding: FragmentRestaurantDetailsBinding
    private val args: RestaurantDetailsFragmentArgs by navArgs()
    private lateinit var data: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productList: ArrayList<ProductsByRestaurantData>

    private var restaurantId: Int = 0
    private var isComeBackFromProductDetails: Boolean = false
    private var productRemovedFromCart: ProductsByRestaurantData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRestaurantDetailsBinding.inflate(layoutInflater)
        setFragmentResultListener(REQUEST_FROM_RESTAURANT_DETAILS) { requestKey, bundle ->
            if (requestKey == REQUEST_FROM_RESTAURANT_DETAILS) {
                isComeBackFromProductDetails = bundle.getBoolean(IS_FROM_PRODUCT_DETAILS, false)
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //call restaurant details api call
        if (!isComeBackFromProductDetails) {
            callRestaurantDetailsApi(data)
        }
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantId = args.restaurantId
        setUpHeader()
        setUpHeaderItemClick()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
        hideContent()
        binding.clTopPart.setRandomBackgroundColor()

        observeRestaurantDetailsResponse()
        observeProductsByRestaurantResponse()
        observeAddUpdateCartResponse()
        observeRemoveFromCartResponse()
        observeGetCartResponse()
        customProgressDialog.show()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            isComeBackFromProductDetails = false
            callRestaurantDetailsApi(data)
        }
    }

    // hide content when not get in api
    private fun hideContent() {
        binding.apply {
            clTopPart.visible(false)
            clRestaurantName.visible(false)
            tvRestaurantAddress.visible(false)
        }
    }

    // show content when not get in api
    private fun showContent() {
        binding.apply {
            clTopPart.visible(true)
            clRestaurantName.visible(true)
            tvRestaurantAddress.visible(true)
        }
    }

    // show header component hide show
    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.restaurant_details)
                ivBack.visible(true)
                ivFavorite.visible(true)
                ivSearch.visible(false)
                frShoppingCart.visible(true)
            }
        }
    }

    // click to favourite button click hide show
    private fun setUpFavButton(isRestaurantExist: Boolean) {
        if (isRestaurantExist) {
            enableFavButton()
        } else {
            disableFavButton()
        }
    }

    // set favourite button show
    private fun enableFavButton() {
        binding.homeDetailsScreenHeader.ivFavorite.setColorFilter(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_red
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )

    }

    // set favourite button hide
    private fun disableFavButton() {
        binding.homeDetailsScreenHeader.ivFavorite.setColorFilter(
            ContextCompat.getColor(
                requireActivity(),
                R.color.icon_color
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )

    }

    // this function handle toolbar component item click
    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }

                val prefRestaurantListStr =
                    (activity as HomeActivity).dataStoreViewModel.getFavoriteRestaurantList()

                val currentRestaurantId = restaurantId.toString()
                val array = (activity as HomeActivity).favRestaurantArray

                if (prefRestaurantListStr != null) {

                    // Retrieving Json Array of type FavRestaurants from preference string
                    val arrayType = object : TypeToken<ArrayList<FavRestaurants>>() {}.type
                    val prefRestaurantList: ArrayList<FavRestaurants> =
                        Gson().fromJson(prefRestaurantListStr, arrayType)

                    // add all preference array to our ArrayList
                    if (prefRestaurantList.isNotEmpty()) {
                        array.clear()
                        array.addAll(prefRestaurantList)
                    }
                }

                val isRestaurantExist = array.any { it.id == currentRestaurantId }

                setUpFavButton(isRestaurantExist)

                ivFavorite.setOnClickListener {
                    val isFav = array.any { it.id == currentRestaurantId }
                    if (isFav) {
                        disableFavButton()
                        // If Restaurant exist then remove it from an Array
                        val data =
                            Predicate { favRestaurant: FavRestaurants -> favRestaurant.id == currentRestaurantId }
                        removeElementByMatchingCriteria(array, data)
                        requireActivity().showLongToast(getString(R.string.msg_restaurant_added_in_fav_list))

                    } else {
                        enableFavButton()
                        // If Restaurant not exist then add it to an Array
                        array.add(
                            FavRestaurants(
                                restaurantId.toString(),
                                true
                            )
                        )
                        requireActivity().showLongToast(getString(R.string.msg_restaurant_removed_from_fav_list))
                    }

                    // Converting ArrayList to Json String and store in preference
                    val data = Gson().toJson(array)
                    (activity as HomeActivity).dataStoreViewModel.saveFavoriteRestaurantList(data)
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
                            ?.navigate(RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToCartListFragment())
                    }

                }
            }
        }
    }

    // set data recyclerview
    private fun setUpRecyclerView() {
        // Products
        productList = ArrayList()
        productListAdapter = ProductListAdapter(productList, this, (activity as HomeActivity))
        binding.rvProducts.apply {
            adapter = productListAdapter
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager =
                LinearLayoutManager(requireActivity())
        }
    }

    //show message dialog when response any throw error
    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    // ============== Call Restaurant Details API
    private fun callRestaurantDetailsApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getRestaurantDetails(
            getHeaderMap(
                data.token,
                true
            ),
            id = restaurantId
        )
    }

    // ============== Observe Restaurant Details Response
    private fun observeRestaurantDetailsResponse() {
        (activity as HomeActivity).homeViewModel.restaurantDetailsResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    showContent()
                    customProgressDialog.dismiss()
                    response.data?.let { restaurantData ->
                        binding.clTopPart.visible(true)
                        with(binding) {
                            tvRestaurantName.text = restaurantData.data.name
                            tvRestaurantAddress.text = restaurantData.data.address

                            Glide.with(ivMenuItem)
                                .load(restaurantData.data.imageUrl)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(android.R.drawable.ic_dialog_alert)
                                .into(ivMenuItem)
                        }

                        // Product List
                        setUpRecyclerView()
                        if (!isComeBackFromProductDetails) {
                            callProductsByRestaurantApi(restaurantData.data.id)
                        }
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


    // ============== Call Products By Restaurant api
    private fun callProductsByRestaurantApi(id: Int) {

        val parameter = Parameters(
            restaurantId = id,
            pageNo = 1,
            limit = 10
        )

        (activity as HomeActivity).homeViewModel.getProductsByRestaurant(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )
    }

    // ============== Observe Products By Restaurant Response
    private fun observeProductsByRestaurantResponse() {
        (activity as HomeActivity).homeViewModel.productsByRestaurant.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { productListResponse ->
                        setUpProductListUI(binding, productListResponse)
                        callGetCartApi()
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

    // set product list in recyclerview for specific restaurant
    private fun setUpProductListUI(
        binding: FragmentRestaurantDetailsBinding,
        productListResponse: ProductsByRestaurantResponse,
    ) {
        binding.apply {
            productList.clear()
            noDataFound.clNoDataFound.visible(true)
            rvProducts.visible(false)
            if (productListResponse.data.isNotEmpty()) {
                rvProducts.visible(true)
                noDataFound.clNoDataFound.visible(false)
                productList.addAll(productListResponse.data.toList())
                productListAdapter.notifyDataSetChanged()
            } else {
                noDataFound.clNoDataFound.visible(true)
                rvProducts.visible(false)
            }
        }
    }

    //show message dialog when response any throw error
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
        (activity as HomeActivity).homeViewModel.getCart(
            getHeaderMap(
                data.token,
                true
            )
        )
    }

    // ============== Observe Cart Response
    @ExperimentalBadgeUtils
    @SuppressLint("LongLogTag")
    private fun observeGetCartResponse() {
        (activity as HomeActivity).homeViewModel.getCartResponse.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { cartList ->
                        handleCartBadgeCount(
                            cartList, (activity as HomeActivity),
                            binding.homeDetailsScreenHeader.frShoppingCart
                        )

                        val productsByRestaurant =
                            (activity as HomeActivity).homeViewModel.productsByRestaurant.value
                        if (productsByRestaurant != null) {
                            if (productsByRestaurant.data != null) {
                                val productListResponse = productsByRestaurant.data
                                if (productListResponse.data.size > 0) {
                                    if (cartList.data.size > 0) {
                                        for (i in 0 until productListResponse.data.size) {
                                            val product = productListResponse.data[i]
                                            for (j in 0 until cartList.data.size) {
                                                if (cartList.data[j].productId == product.productId) {
                                                    product.totalQty =
                                                        cartList.data[j].quantity.toInt()
                                                    product.cartId = cartList.data[j].id
                                                    product.isCartExist = true
                                                    break
                                                }
                                            }
                                        }
                                    } else {
                                        for (i in 0 until productListResponse.data.size) {
                                            val product = productListResponse.data[i]
                                            product.totalQty = 0
                                            product.isCartExist = false
                                        }
                                    }
                                    productListAdapter.notifyDataSetChanged()
                                }
                            }
                        }
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

    // ================ ADD OR REMOVE PRODUCT FROM CART

    private fun callAddUpdateToCartApi(
        productOrCartId: String,
        qty: String,
        amount: String,
        isCartExist: Boolean,
    ) {
//        customProgressDialog.show()
        if (isCartExist) {
            val parameter = AddUpdateCartWithCartId(
                id = productOrCartId,
                quantity = qty,
                amount = amount
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
                productId = productOrCartId,
                quantity = qty,
                amount = amount
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

    // ============== Observe Products By Restaurant Response
    private fun observeAddUpdateCartResponse() {
        (activity as HomeActivity).homeViewModel.addUpdateToCart.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        callGetCartApi()
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

    override fun onItemClick(data: ProductsByRestaurantData, position: Int) {
        val bundle = Gson().toJson(data)
        view?.findNavController()?.navigate(
            RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToProductDetailsFragment(
                data.productId,
                Constants.RESTAURANT,
                bundle
            )
        )
    }

    // + Button click
    override fun onAddProductButtonClick(
        data: ProductsByRestaurantData,
        position: Int,
    ) {
        data.totalQty = data.totalQty + 1
        callAddUpdateToCartApi(
            productOrCartId = data.cartId.toString(),
            qty = data.totalQty.toString(),
            amount = data.price.toString(),
            isCartExist = data.isCartExist
        )
    }

    // Minus Button Click
    override fun onRemoveProductButtonClick(
        data: ProductsByRestaurantData,
        position: Int,
    ) {
        data.totalQty = data.totalQty - 1
        // when quantity
        if (data.totalQty == 0) {
            // Call Remove Cart API
            callRemoveFromCartApi(data.cartId)
            productRemovedFromCart = data

        } else {
            callAddUpdateToCartApi(
                productOrCartId = data.cartId.toString(),
                qty = data.totalQty.toString(),
                amount = data.price.toString(),
                isCartExist = data.isCartExist
            )

        }

    }

    //remove all item from cart when user add different restaurant item in cart so remove previous item all remove then after
    // add into cart
    override fun onRemoveAllCartData(cartId: Int) {
        callRemoveFromCartApi(cartId)
    }

    //remove single item from cart api call
    private fun callRemoveFromCartApi(cartId: Int) {
        val parameter = RemoveFromCartParam(cartId = cartId)
        (activity as HomeActivity).homeViewModel.removeFromCart(getHeaderMap(data.token, true), Gson().toJson(parameter))
    }

    // Add button click to first time add item in cart
    override fun onAddButtonClick(
        data: ProductsByRestaurantData,
        position: Int,
    ) {
        data.totalQty = 1
        if (data.isCartExist) {
            //when item already in cart and quantity is 1 or more available call this api
            callAddUpdateToCartApi(
                productOrCartId = data.cartId.toString(),
                qty = data.totalQty.toString(),
                amount = data.price.toString(),
                isCartExist = true
            )
        } else {
            //when item not in cart and quantity is not available call this api
            callAddUpdateToCartApi(
                productOrCartId = data.productId.toString(),
                qty = data.totalQty.toString(),
                amount = data.price.toString(),
                isCartExist = false
            )
        }
    }

    //Observe remove cart from list response
    private fun observeRemoveFromCartResponse() {
        (activity as HomeActivity).homeViewModel.removeFromCart.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.data == 1) {
                            val productsByRestaurant =
                                (activity as HomeActivity).homeViewModel.productsByRestaurant.value
                            if (productsByRestaurant != null) {
                                if (productsByRestaurant.data != null) {
                                    val productListResponse = productsByRestaurant.data
                                    if (productListResponse.data.size > 0) {
                                        for (i in 0 until productListResponse.data.size) {
                                            if (productRemovedFromCart != null) {
                                                if (productListResponse.data[i].productId == productRemovedFromCart?.productId) {
                                                    productListResponse.data[i].totalQty = 0
                                                    productListResponse.data[i].isCartExist = false
                                                    callGetCartApi()
                                                    break
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }
}