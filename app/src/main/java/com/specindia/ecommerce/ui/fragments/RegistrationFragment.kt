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
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentRegistrationBinding
import com.specindia.ecommerce.util.emptyEditText
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.etPassword
import kotlinx.android.synthetic.main.fragment_registration.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpannableText()
        startEditTextSpace()
        binding.btnRegister.setOnClickListener {
            if (isEmpty()) {
                view.findNavController().popBackStack()
            }
        }

    }

    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.already_have_an_account_login))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
            }
        }
        spanText.setSpan(
            clickableString,
            25,
            spanText.length,
            30
        )
        spanText.setSpan(
            ForegroundColorSpan(Color.RED),
            25, // start
            30, // end
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.apply {
            tvAlreadyHaveAnAccount.setText(spanText, TextView.BufferType.SPANNABLE)
            tvAlreadyHaveAnAccount.movementMethod = LinkMovementMethod.getInstance();
        }

    }

    private fun startEditTextSpace() {
        with(binding) {
            etName.emptyEditText(etName)
            etEmail.emptyEditText(etEmail)
            etMobileNo.emptyEditText(etMobileNo)
            etAddress.emptyEditText(etAddress)
            etPassword.emptyEditText(etPassword)
            etConfirmPassword.emptyEditText(etConfirmPassword)
        }

    }

    private fun isEmpty(): Boolean {
        binding.apply {
            if (etName.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollviewSignup, "Please enter name")
                return false
            } else if (etEmail.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollviewSignup, "Please enter email")
                return false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim())
                    .matches()
            ) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    "Please enter valid email"
                )
                return false
            } else if (etMobileNo.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    "Please enter mobile number"
                )
                return false
            } else if (etMobileNo.text.toString().trim().length < 10) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    "Please enter valid mobile number"
                )
                return false
            } else if (etAddress.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollviewSignup, "Please enter address")
                return false
            } else if (etPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollviewSignup, "Please enter password")
                return false
            } else if (!isValidPassword(etPassword.text.toString().trim())) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    "Password minimum 8 character with at least one uppercase,one small case,one special symbol"
                )
                return false
            } else if (etConfirmPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    "Please enter confirm password"
                )
                return false
            } else if (etPassword.text?.trim().toString() != etConfirmPassword.text?.trim()
                    .toString()
            ) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    "Password doesn't match"
                )
                return false
            }
        }
        return true
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }
}