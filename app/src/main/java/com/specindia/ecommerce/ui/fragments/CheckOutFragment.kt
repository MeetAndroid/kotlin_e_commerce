package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentCheckoutBinding
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckOutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCheckoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
        setUpData()
       // (activity as HomeActivity).showOrHideBottomAppBarAndFloatingActionButtonOnScroll()
    }

    private fun setUpData() {
        binding.apply {
            tvAddress.text =
                "NH 8C, Sarkhej - Gandhinagar Hwy, opp. Gurudwara, Bodakdev, Ahmedabad, Gujarat 380054"
            ctvPayPal.text="testUser@paypal.com"
            tvSubTotal.text = "Rs. 68"
            tvDeliveryCost.text = "Rs. 2"
            tvDiscount.text = "- Rs. 4"
            tvTotal.text = "Rs. 66"
        }
    }


    private fun setUpHeader() {
        with(binding) {
            with(orderDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.check_out)
                ivBack.visible(true)
                ivFavorite.visible(false)
                frShoppingCart.visible(false)
                ivSearch.visible(false)
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