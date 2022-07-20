package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.databinding.FragmentSearchBinding
import com.specindia.ecommerce.util.visible

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
    }

    private fun setUpHeader() {
        with(binding) {
            with(homeMenuScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.visible(false)
                ivBack.visible(true)
                ivFavorite.visible(false)
                ivShoppingCart.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(homeMenuScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }
}