package com.specindia.ecommerce.ui.authentication.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.spec.spec_ecommerce.databinding.FragmentResetPasswordBinding
import com.specindia.ecommerce.util.emptyEditText
import com.specindia.ecommerce.util.isValidPassword
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            etPassword.emptyEditText(etPassword)
            etConfirmPassword.emptyEditText(etConfirmPassword)
            btnResetPassword.setOnClickListener {
                if (isEmpty()) {
                    view.findNavController().popBackStack()
                }
            }
        }
    }

    private fun isEmpty(): Boolean {

        binding.apply {
            if (etPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), llResetPassword, "Please enter password")
                return false
            } else if (!isValidPassword(etPassword.text.toString().trim())) {
                showMaterialSnack(
                    requireContext(),
                    llResetPassword,
                    "Password minimum 8 character with at least one uppercase,one small case,one special symbol"
                )
                return false
            } else if (etConfirmPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    llResetPassword,
                    "Please enter confirm password"
                )
                return false
            } else if (etPassword.text?.trim().toString() != etConfirmPassword.text?.trim()
                    .toString()
            ) {
                showMaterialSnack(
                    requireContext(),
                    llResetPassword,
                    "Password doesn't match"
                )
                return false
            }
        }
        return true
    }

}