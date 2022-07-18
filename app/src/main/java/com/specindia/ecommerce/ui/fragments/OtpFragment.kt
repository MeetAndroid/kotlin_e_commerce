package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentOtpBinding
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpFragment : Fragment() {

    var otp = ""
    private lateinit var binding: FragmentOtpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.get("key_email")
        binding.apply {
            tvEmail.text = email.toString()

            pinView.onTextChangedListener = { text ->
                if (text != null) {
                    otp = text
                }
            }

            btnOtpVerify.setOnClickListener {
                if (otp == "") {
                    showMaterialSnack(requireContext(), llOtp, "Please enter otp")
                } else if (otp.length < 6) {
                    showMaterialSnack(requireContext(), llOtp, "Please enter valid otp")
                } else {
                    view.findNavController()
                        .navigate(R.id.action_otpFragment_to_resetPasswordFragment)
                }
            }
        }


    }
}