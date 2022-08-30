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
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentCartListBinding
import com.specindia.ecommerce.models.request.AddUpdateCartWithCartId
import com.specindia.ecommerce.models.request.RemoveFromCartParam
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.cart.getcart.GetCartData
import com.specindia.ecommerce.models.response.cart.getcart.GetCartResponse
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.CartListAdapter
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartListFragment : Fragment(), CartListAdapter.OnCartItemClickListener {

    private lateinit var binding: FragmentCartListBinding
    private lateinit var user: AuthResponseData
    private lateinit var customProgressDialog: AlertDialog

    private lateinit var cartListAdapter: CartListAdapter
    private lateinit var cartList: ArrayList<GetCartData>

    private var total: Int = 0
    private var subTotal: Int = 0
    private var deliveryCost: Int = 0
    private var discount: Int = 0
    private var specWallet: Int = 0

    private var restaurantId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartListBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeader()
        setUpHeaderItemClick()
        setUpButtonClick()
        setUpRecyclerView()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        user = Gson().fromJson(userData, AuthResponseData::class.java)

        callGetCartApi()
        observeGetCartResponse()
        observeAddUpdateCartResponse()
        observeRemoveFromCartResponse()

        //(activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()

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
                frShoppingCart.visible(false)
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

    private fun setUpButtonClick() {
        with(binding) {
            btnCheckOut.setOnClickListener {
                it.findNavController()
                    .navigate(
                        CartListFragmentDirections.actionCartListFragmentToCheckOutFragment(
                            total = total,
                            subTotal = subTotal,
                            extraCharges = deliveryCost,
                            restaurantId = restaurantId
                        )
                    )
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
            addItemDecoration(
                MarginDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_margin_16),
                    false
                )
            )
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

    @ExperimentalBadgeUtils
    private fun setUpCartListUI(
        binding: FragmentCartListBinding,
        cartListResponse: GetCartResponse,
    ) {
        binding.apply {
            cartList.clear()
            noDataFound.clNoDataFound.visible(true)
            swipeRefreshLayout.visible(false)

            if (cartListResponse.data.isNotEmpty()) {

                calculateAndDisplayTotal(cartListResponse.data)

                swipeRefreshLayout.visible(true)
                noDataFound.clNoDataFound.visible(false)
                cartList.addAll(cartListResponse.data.toList())
                cartListAdapter.notifyDataSetChanged()
            } else {
                noDataFound.clNoDataFound.visible(true)
                swipeRefreshLayout.visible(false)
            }
        }
    }

    private fun calculateAndDisplayTotal(cartList: ArrayList<GetCartData>) {
        subTotal =
            cartList.sumOf { (it.quantity.toInt() * it.amount.toInt()) }

        deliveryCost = 50
        discount = 4
        specWallet = 10
        total = subTotal + deliveryCost - discount - specWallet
        restaurantId = cartList[0].product.restaurantId

        binding.apply {
            tvSubTotal.text = getString(R.string.rs, subTotal.toString())
            tvDeliveryCost.text = getString(R.string.rs, deliveryCost.toString())
            tvDiscount.text = getString(R.string.minus_rs, discount.toString())
            tvSpecWallet.text = getString(R.string.minus_rs, specWallet.toString())
            tvTotal.text = getString(R.string.rs, total.toString())
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
                user.token,
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
                    response.data?.let { cartListResponse ->
                        setUpCartListUI(binding, cartListResponse)
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

    override fun onItemClick(data: GetCartData, position: Int) {

    }

    override fun onAddProductButtonClick(data: GetCartData, position: Int) {
        var qty = data.quantity.toInt()
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
        )
    }

    override fun onRemoveProductButtonClick(data: GetCartData, position: Int) {

        if (data.quantity == "1") {
            customProgressDialog.show()

            val parameter = RemoveFromCartParam(
                cartId = data.id
            )

            (activity as HomeActivity).homeViewModel.removeFromCart(
                getHeaderMap(
                    user.token,
                    true
                ),
                Gson().toJson(parameter)
            )

        } else {
            var qty = data.quantity.toInt()
            qty -= 1
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
            )
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

    // ============== Observe Products Remove By Cart Response
    private fun observeRemoveFromCartResponse() {
        (activity as HomeActivity).homeViewModel.removeFromCart.observe(
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
                    customProgressDialog.show()
                }
            }
        }
    }
}