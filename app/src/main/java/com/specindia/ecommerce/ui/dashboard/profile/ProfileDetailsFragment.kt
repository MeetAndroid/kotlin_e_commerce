package com.specindia.ecommerce.ui.dashboard.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentProfileDetailsBinding
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProfileDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpHeaderItemClick()
    }

    private fun setUpHeader() {
        with(binding) {
            with(profileDetailsScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(R.string.profile_details)
                ivBack.visible(true)
                ivFavorite.visible(false)
                frShoppingCart.visible(false)
                ivSearch.visible(false)
            }
        }
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            with(profileDetailsScreenHeader) {
                ivBack.setOnClickListener {
                    it.findNavController().popBackStack()
                }
            }
        }
    }
}