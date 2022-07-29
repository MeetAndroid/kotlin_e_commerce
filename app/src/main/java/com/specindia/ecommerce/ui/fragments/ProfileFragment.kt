package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentProfileBinding
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeader()
        setUpProfileData()

        binding.btnProfileDetails.setOnClickListener {
            view.findNavController()
                .navigate(ProfileFragmentDirections.actionProfileFragmentToProfileDetailsFragment())
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
                ivSearch.visible(false)
            }
        }
    }

    private fun setUpProfileData() {

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        val data = Gson().fromJson(userData, AuthResponseData::class.java)
        Log.d("LoggedInData", data.toString())


        with(binding) {
            Glide.with(ivProfileImage)
                .load((activity as HomeActivity).dataStoreViewModel.getProfileUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.ic_dialog_alert)
                .into(ivProfileImage)
            tvToken.text = data?.token
            tvId.text = data?.id.toString()
            tvFirstName.text = data?.firstName
            tvLastName.text = data?.lastName
            tvEmail.text = data?.email
        }

//
//        with(binding) {
//            with((activity as HomeActivity).dataStoreViewModel) {
//
//                Glide.with(ivProfileImage).load(getProfileUrl()).into(ivProfileImage)
//
//                tvToken.text = getFBAccessToken()
//                tvId.text = getUserId()
//                tvFirstName.text = getFirstName()
//                tvLastName.text = getLastName()
//                tvEmail.text = getEmail()
//            }
//        }
    }
}