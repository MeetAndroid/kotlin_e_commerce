package com.specindia.ecommerce.ui.dashboard.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentMoreBinding
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        return binding.root
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick()
        setUpHeader()
        setUpHeaderItemClick()
        //(activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()


        binding.clMyOrder.setOnClickListener {
            if (!requireActivity().isConnected) {
                showMaterialSnack(
                    requireContext(),
                    it,
                    getString(R.string.message_no_internet_connection)
                )

            } else {
                view.findNavController().navigate(
                    MoreFragmentDirections.actionMoreFragmentToOrderHistoryFragment()
                )
            }

        }
    }

    @ExperimentalBadgeUtils
    private fun setUpHeader() {
        with(binding) {
            with(MoreScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.more)
                ivBack.visible(false)
                ivFavorite.visible(false)
                frShoppingCart.visible(true)
                ivSearch.visible(false)
                updateCartCountOnUI((activity as HomeActivity), frShoppingCart)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(MoreScreenHeader) {
                frShoppingCart.setOnClickListener {
                    if (!requireActivity().isConnected) {
                        showMaterialSnack(
                            requireContext(),
                            it,
                            getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        view?.findNavController()
                            ?.navigate(MoreFragmentDirections.actionMoreFragmentToCartListFragment())
                    }

                }
            }
        }
    }

    private fun setUpButtonClick() {
        binding.clLogout.setOnClickListener {
            showLogoutDialog(activity as HomeActivity)
        }
    }

    // logout for user
    private fun showLogoutDialog(activity: HomeActivity) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.app_name))
            .setCancelable(false)
            .setMessage(activity.getString(R.string.msg_confirm_logout))
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                logout()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->

            }
            .show()
    }
}