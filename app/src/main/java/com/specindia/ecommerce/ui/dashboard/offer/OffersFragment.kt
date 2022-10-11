package com.specindia.ecommerce.ui.dashboard.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentOffersBinding
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OffersFragment : Fragment() {

    private lateinit var binding: FragmentOffersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOffersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
    }

    private fun setUpHeader() {
        with(binding) {
            with(offersScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.offers)
                ivBack.visible(false)
                ivFavorite.visible(false)
                frShoppingCart.visible(false)
                ivSearch.visible(false)
            }
        }
    }
}