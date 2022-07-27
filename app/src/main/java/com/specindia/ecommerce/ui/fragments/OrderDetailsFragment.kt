package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentOrderDetailsBinding
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
        setUpData()
    }

    private fun setUpData() {
        binding.apply {
            Glide.with(binding.ivProductImage)
                .load("https://picsum.photos/200")
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.ic_dialog_alert)
                .into(binding.ivProductImage)

            tvProductName.text = "Mulberry Pizza by Josh"
            tvProductPrice.text = "250.00 Rs"
            tvOrderCount.text = "2"
        }

    }


    private fun setUpHeader() {
        with(binding) {
            with(orderDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.order_details)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivShoppingCart.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(orderDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }
}