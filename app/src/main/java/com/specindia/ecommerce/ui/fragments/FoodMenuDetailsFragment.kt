package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.databinding.FragmentFoodMenuDetailsBinding
import com.specindia.ecommerce.util.showLongToast
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodMenuDetailsFragment : Fragment() {

    private lateinit var binding: FragmentFoodMenuDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodMenuDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(foodMenuDetailsScreenHeader) {

                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }

                ivFavorite.setOnClickListener {
                    requireActivity().showLongToast("Add to Favorite")
                }
                ivShoppingCart.setOnClickListener {
                    requireActivity().showLongToast("Add to Cart")
                }
            }
        }
    }

    private fun setUpHeader() {
        with(binding) {
            with(foodMenuDetailsScreenHeader) {
                tvHeaderTitle.visible(false)
                ivBack.visible(true)
                ivFavorite.visible(true)
                ivShoppingCart.visible(true)
            }
        }
    }
}