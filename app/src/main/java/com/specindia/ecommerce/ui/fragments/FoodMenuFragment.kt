package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentFoodMenuBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.menulist.Menu
import com.specindia.ecommerce.models.response.menulist.MenuListResponse
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.MenuListAdapter
import com.specindia.ecommerce.util.SpaceItemDecoration
import com.specindia.ecommerce.util.getHeaderMap
import com.specindia.ecommerce.util.showLongToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodMenuFragment : Fragment() {

    private lateinit var binding: FragmentFoodMenuBinding
    private lateinit var data: AuthResponseData
    private lateinit var menuListAdapter: MenuListAdapter

    private lateinit var menuList: ArrayList<Menu>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)

        setUpRecyclerView()
        callMenuListApi(data)
        observeResponse()

        binding.btnFoodMenuDetails.setOnClickListener {
            it.findNavController().navigate(R.id.action_foodMenuFragment_to_foodMenuDetailsFragment)
        }

        menuListAdapter.setOnItemClickListener {
            requireActivity().showLongToast("${it.name} clicked")
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
//            startShimmerEffect(binding)
            callMenuListApi(data)
        }
    }

    private fun setUpRecyclerView() {
        // Menus
        menuList = ArrayList()
        menuListAdapter = MenuListAdapter(requireContext(), menuList)
        binding.rvMenu.apply {
            adapter = menuListAdapter
//            addItemDecoration(SpaceItemDecoration(10, false))
            layoutManager =
                GridLayoutManager(requireActivity(), 2)
        }
    }

    private fun setUpHeader() {
        with(binding) {
            with(foodMenuScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.menu)
                ivBack.visible(false)
                ivFavorite.visible(false)
                ivShoppingCart.visible(true)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(foodMenuScreenHeader) {
                ivShoppingCart.setOnClickListener {
                    requireActivity().showLongToast("Add to cart")
                }
            }
        }
    }


    private fun callMenuListApi(data: AuthResponseData) {
        (activity as HomeActivity).homeViewModel.getMenuList(getHeaderMap(data.token, true))
    }

    private fun observeResponse() {
        Log.d("ObserveResponse", "True")
        (activity as HomeActivity).homeViewModel.menuListResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { menuListResponse ->
//                        stopShimmerEffect(binding)
                        setUpMenuListUI(binding, menuListResponse)
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

    private fun setUpMenuListUI(
        binding: FragmentFoodMenuBinding,
        menuListResponse: MenuListResponse
    ) {
        binding.apply {
            menuList.clear()
            if (menuListResponse.data.menu.isNotEmpty()) {
                menuList.addAll(menuListResponse.data.menu.toList())
                menuListAdapter.notifyDataSetChanged()
            } else {
                // No Data Found
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