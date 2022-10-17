 package com.specindia.ecommerce.ui.dashboard.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentFoodMenuBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.menulist.Menu
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.ui.dashboard.menu.adapter.MenuListAdapter
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodMenuFragment : Fragment() {

    private lateinit var binding: FragmentFoodMenuBinding
    private lateinit var data: AuthResponseData
    private lateinit var menuListAdapter: MenuListAdapter
    private lateinit var customProgressDialog: AlertDialog
    private lateinit var menuList: ArrayList<Menu>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFoodMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
        setUpProgressDialog()
        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        setUpRecyclerView()

        if ((activity as HomeActivity).homeViewModel.menuListResponse.value == null) {
            callMenuListApi(data)
        }

        observeResponse()

        menuListAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.name} clicked")
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            callMenuListApi(data)
        }
    }

    private fun setUpRecyclerView() {
        // Menus
        menuList = ArrayList()
        menuListAdapter = MenuListAdapter(menuList)
        binding.rvMenu.apply {
            adapter = menuListAdapter
            addItemDecoration(MarginDecoration(resources.getDimensionPixelSize(R.dimen.item_margin),
                true))
            hasFixedSize()
        }
    }

    @ExperimentalBadgeUtils
    private fun setUpHeader() {
        with(binding) {
            with(foodMenuScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.menu)
                ivBack.visible(false)
                ivSearch.visible(false)
                ivFavorite.visible(false)
                frShoppingCart.visible(true)
                updateCartCountOnUI((activity as HomeActivity), frShoppingCart)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(foodMenuScreenHeader) {
                frShoppingCart.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        view?.findNavController()
                            ?.navigate(FoodMenuFragmentDirections.actionFoodMenuFragmentToCartListFragment())
                    }

                }
            }
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    // call menu list api
    private fun callMenuListApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getMenuList(getHeaderMap(data.token, true))
    }

    //Observe menu list
    private fun observeResponse() {
        (activity as HomeActivity).homeViewModel.menuListResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let { menuListResponse ->
                        setUpMenuListUI(binding, menuListResponse)
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

    private fun setUpMenuListUI(
        binding: FragmentFoodMenuBinding,
        menuListResponse: MenuListResponse,
    ) {
        binding.apply {
            menuList.clear()
            noDataFound.clNoDataFound.visible(true)
            clMenuList.visible(false)
            if (menuListResponse.data.menu.isNotEmpty()) {
                clMenuList.visible(true)
                noDataFound.clNoDataFound.visible(false)
                menuList.addAll(menuListResponse.data.menu.toList())
                menuListAdapter.notifyDataSetChanged()
            } else {
                noDataFound.clNoDataFound.visible(true)
                clMenuList.visible(false)
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