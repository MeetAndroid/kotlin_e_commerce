package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.facebook.login.LoginManager
import com.specindia.ecommerce.databinding.FragmentMoreBinding
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.logout
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick()
        setUpHeader()
        //(activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()


        binding.clMyOrder.setOnClickListener {
            view.findNavController().navigate(
                MoreFragmentDirections.actionMoreFragmentToOrderHistoryFragment()
            )
        }
    }

    private fun setUpHeader() {
        with(binding) {
            with(MoreScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(com.specindia.ecommerce.R.string.more)
                ivBack.visible(false)
                ivFavorite.visible(false)
                ivShoppingCart.visible(true)
                ivSearch.visible(false)
            }
        }
    }

    private fun setUpButtonClick() {
        binding.clLogout.setOnClickListener {
            logout()
        }
    }
}