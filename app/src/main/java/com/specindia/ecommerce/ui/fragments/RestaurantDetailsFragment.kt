package com.specindia.ecommerce.ui.fragments

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentRestaurantDetailsBinding
import com.specindia.ecommerce.models.FavRestaurants
import com.specindia.ecommerce.models.ProductWithTotalQty
import com.specindia.ecommerce.models.request.AddUpdateCartWithCartId
import com.specindia.ecommerce.models.request.AddUpdateCartWithProductId
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantResponse
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.ProductListAdapter
import com.specindia.ecommerce.util.*
import com.specindia.ecommerce.util.Constants.Companion.IS_FROM_PRODUCT_DETAILS
import com.specindia.ecommerce.util.Constants.Companion.REQUEST_FROM_RESTAURANT_DETAILS
import dagger.hilt.android.AndroidEntryPoint


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
    private var productWithTotalQtyList = ArrayList<ProductWithTotalQty>()

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
        if (!isComeBackFromProductDetails) {
            callRestaurantDetailsApi(data)
        }
    }

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

        (activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callRestaurantDetailsApi(data)
        }

    }

    private fun hideContent() {
        binding.apply {
            clTopPart.visible(false)
            clRestaurantName.visible(false)
            tvRestaurantAddress.visible(false)
        }
    }

    private fun showContent() {
        binding.apply {
            clTopPart.visible(true)
            clRestaurantName.visible(true)
            tvRestaurantAddress.visible(true)
        }
    }

    private fun setUpHeader() {
        with(binding) {
            with(homeDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.restaurant_details)
                ivBack.visible(true)
                ivFavorite.visible(true)
                ivSearch.visible(false)
                ivShoppingCart.visible(true)
            }
        }
    }

    private fun setUpFavButton(isRestaurantExist: Boolean) {
        if (isRestaurantExist) {
            enableFavButton()
        } else {
            disableFavButton()
        }
    }

    private fun enableFavButton() {
        binding.homeDetailsScreenHeader.ivFavorite.setColorFilter(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_red
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )

    }

    private fun disableFavButton() {
        binding.homeDetailsScreenHeader.ivFavorite.setColorFilter(
            ContextCompat.getColor(
                requireActivity(),
                R.color.icon_color
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )

    }

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
            }
        }
    }


    private fun setUpRecyclerView() {
        // Products
        productList = ArrayList()
        productListAdapter = ProductListAdapter(productList, this)
        binding.rvProducts.apply {
            adapter = productListAdapter
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
                    customProgressDialog.hide()
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

                        observeProductsByRestaurantResponse()
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
                    customProgressDialog.hide()
                    response.data?.let { productListResponse ->
                        setUpProductListUI(binding, productListResponse)

                        callGetCartApi()
                        observeGetCartResponse()
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
                    response.data?.let { cartList ->
                        if (cartList.data.size > 0) {

                            val productsByRestaurant =
                                (activity as HomeActivity).homeViewModel.productsByRestaurant.value
                            if (productsByRestaurant != null) {
                                if (productsByRestaurant.data != null) {
                                    val productListResponse = productsByRestaurant.data

                                    productWithTotalQtyList.clear()
                                    productWithTotalQtyList = ArrayList()


                                    if (productListResponse.data.size > 0) {
                                        for (i in 0 until productListResponse.data.size) {
                                            val productList = productListResponse.data

                                            for (j in 0 until cartList.data.size) {
                                                if (cartList.data[j].productId == productList[i].id) {
                                                    // cart has a specific product
                                                    productWithTotalQtyList.add(
                                                        ProductWithTotalQty(
                                                            productId = cartList.data[j].productId.toString(),
                                                            totalQty = cartList.data[j].quantity,
                                                            cartId = cartList.data[j].id.toString(),
                                                            positionInAdapter = i,
                                                            isCartExist = true))
                                                }

                                            }

                                        }
                                    }

                                    productWithTotalQtyList.trimToSize()
                                    if (productWithTotalQtyList.size > 0) {
                                        for (k in 0 until productWithTotalQtyList.size) {
                                            val currentData = productWithTotalQtyList[k]
                                            val positionInAdapter =
                                                productWithTotalQtyList[k].positionInAdapter
                                            productListResponse.data[positionInAdapter].totalQty =
                                                currentData.totalQty.toInt()
                                            productListResponse.data[positionInAdapter].cartId =
                                                currentData.cartId.toInt()
                                            productListResponse.data[positionInAdapter].isCartExist =
                                                currentData.isCartExist
                                        }
                                        productListAdapter.notifyDataSetChanged()
                                    }

                                }

                            }
                        }

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

    private fun callAddUpdateToCartApi(
        productOrCartId: String,
        qty: String,
        amount: String,
        isCartExist: Boolean,
    ) {
        customProgressDialog.show()
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
    private fun observeAddUpdateCartResponse(isProductAdded: Boolean) {
        (activity as HomeActivity).homeViewModel.addUpdateToCart.observe(
            viewLifecycleOwner
        ) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.hide()
                    response.data?.let {
                        if (isProductAdded) {
                            requireActivity().showShortToast(getString(R.string.msg_product_added_to_cart))
                        } else {
                            requireActivity().showShortToast(getString(R.string.product_removed_from_cart))
                        }
                        callGetCartApi()
                        observeGetCartResponse()
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

    override fun onItemClick(data: ProductsByRestaurantData, position: Int) {
        view?.findNavController()
            ?.navigate(
                RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToProductDetailsFragment(
                    data.id, Constants.RESTAURANT
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

        observeAddUpdateCartResponse(true)
    }

    // - Button Click
    override fun onRemoveProductButtonClick(
        data: ProductsByRestaurantData,
        position: Int,
    ) {
        data.totalQty = data.totalQty - 1

        callAddUpdateToCartApi(
            productOrCartId = data.cartId.toString(),
            qty = data.totalQty.toString(),
            amount = data.price.toString(),
            isCartExist = data.isCartExist
        )

        observeAddUpdateCartResponse(false)
    }

    // Add Button Click
    override fun onAddButtonClick(
        data: ProductsByRestaurantData,
        position: Int,
    ) {
        data.totalQty = 1
        if (data.isCartExist) {
            callAddUpdateToCartApi(
                productOrCartId = data.cartId.toString(),
                qty = data.totalQty.toString(),
                amount = data.price.toString(),
                isCartExist = true
            )
        } else {
            callAddUpdateToCartApi(
                productOrCartId = data.id.toString(),
                qty = data.totalQty.toString(),
                amount = data.price.toString(),
                isCartExist = false
            )
        }

        observeAddUpdateCartResponse(true)
    }

}