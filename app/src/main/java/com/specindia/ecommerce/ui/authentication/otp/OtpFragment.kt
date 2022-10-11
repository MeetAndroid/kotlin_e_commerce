package com.specindia.ecommerce.ui.authentication.otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentOtpBinding
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpFragment : Fragment() {

    private var otp = ""
    private lateinit var binding: FragmentOtpBinding
    private val args: OtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = args.emailId

        binding.apply {
            tvEmail.text = email.toString()

            pinView.onTextChangedListener = { text ->
                if (text != null) {
                    otp = text
                }
            }

            btnOtpVerify.setOnClickListener {
                if (otp == "") {
                    showMaterialSnack(requireContext(), llOtp, getString(R.string.val_msg_enter_otp))
                } else if (otp.length < 6) {
                    showMaterialSnack(requireContext(), llOtp, getString(R.string.val_msg_enter_valid_otp))
                } else {
                    view.findNavController()
                        .navigate(OtpFragmentDirections.actionOtpFragmentToResetPasswordFragment())
                }
            }
        }

    }
}