package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentForgotPasswordBinding
import com.specindia.ecommerce.util.emptyEditText
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    showMaterialSnack(requireContext(), nestedScrollview, "Please enter email")
                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                        etForgotPasswordEmail.text.toString().trim()
                    )
                        .matches()
                ) {
                    showMaterialSnack(
                        requireContext(),
                        nestedScrollview,
                        "Please enter valid email"
                    )
                } else {
                    val email = binding.etForgotPasswordEmail.text.toString().trim()
                    val bundle = bundleOf("key_email" to email)
                    view.findNavController()
                        .navigate(R.id.action_forgotPasswordFragment_to_otpFragment, bundle)
                }
            }
        }
    }
}