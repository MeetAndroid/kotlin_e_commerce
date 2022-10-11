package com.specindia.ecommerce.ui.authentication.forgotpassword

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentForgotPasswordBinding
import com.specindia.ecommerce.util.emptyEditText
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            etForgotPasswordEmail.emptyEditText(etForgotPasswordEmail)
            btnForgotPassword.setOnClickListener {
                if (etForgotPasswordEmail.text.toString().trim().isEmpty()) {
                    showMaterialSnack(requireContext(),
                        nestedScrollview,
                        getString(R.string.val_msg_enter_address))
                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                        etForgotPasswordEmail.text.toString().trim()
                    )
                        .matches()
                ) {
                    showMaterialSnack(
                        requireContext(),
                        nestedScrollview,
                        getString(R.string.val_msg_enter_valid_email)
                    )
                } else {
                    val email = binding.etForgotPasswordEmail.text.toString().trim()
                    view.findNavController()
                        .navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToOtpFragment(
                            email))
                }
            }
        }
    }
}