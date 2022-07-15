package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentProfileBinding
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpProfileData()

        binding.btnProfileDetails.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_profileDetailsFragment)
        }
    }

    private fun setUpHeader() {
        with(binding) {
            with(profileScreenHeader) {
                tvHeaderTitle.visible(true)
                tvHeaderTitle.text = getString(com.specindia.ecommerce.R.string.profile)
                ivBack.visible(false)
                ivFavorite.visible(false)
                ivShoppingCart.visible(false)
            }
        }
    }

    private fun setUpProfileData() {
        with(binding) {
            with((activity as HomeActivity).dataStoreViewModel) {

                Glide.with(ivProfileImage).load(getProfileUrl()).into(ivProfileImage)

                tvToken.text = getFBAccessToken()
                tvId.text = getUserId()
                tvFirstName.text = getFirstName()
                tvLastName.text = getLastName()
                tvEmail.text = getEmail()
            }
        }
    }
}