package com.specindia.ecommerce.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentLoginBinding
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.viewmodel.DataViewModel
import com.specindia.ecommerce.util.emptyEditText
import com.specindia.ecommerce.util.showMaterialSnack
import com.specindia.ecommerce.util.showToast
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<DataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick(view)
        setSpannableText()
        startEditTextSpace()
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnLogin.setOnClickListener {
                if (isEmpty()) {
                    requireActivity().startNewActivity(HomeActivity::class.java)
                    viewModel.saveUserLoggedIn(true)
                    showToast(requireContext(), "Login Successfully")
                }
            }

            btnForgotPassword.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
    }

    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.don_t_have_an_account_sign_up))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
        }
        spanText.setSpan(
            clickableString,
            22,
            spanText.length,
            30
        )
        spanText.setSpan(
            ForegroundColorSpan(Color.RED),
            22, // start
            spanText.length, // end
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.apply {
            tvDontHaveAnAccount.setText(spanText, TextView.BufferType.SPANNABLE)
            tvDontHaveAnAccount.movementMethod = LinkMovementMethod.getInstance();
        }
    }

    private fun startEditTextSpace() {
        with(binding) {
            etLoginEmail.emptyEditText(etLoginEmail)
            etPassword.emptyEditText(etPassword)
        }
    }

    private fun isEmpty(): Boolean {
        binding.apply {
            if (etLoginEmail.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollview, "Please enter email")
                return false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etLoginEmail.text.toString().trim())
                    .matches()
            ) {
                showMaterialSnack(requireContext(), nestedScrollview, "Please enter valid email")
                return false
            } else if (etPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollview, "Please enter password")
                return false
            }
        }
        return true
    }
}