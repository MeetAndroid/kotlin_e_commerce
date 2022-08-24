package com.specindia.ecommerce.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentRegistrationBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.ui.activity.AuthActivity
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var customProgressDialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpannableText()
        startEditTextSpace()
        setUpProgressDialog()

        binding.btnRegister.setOnClickListener {
            if (isEmpty()) {
                customProgressDialog.show()
                callRegistrationApi(binding)
                observeResponse()
            }
        }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    private fun callRegistrationApi(binding: FragmentRegistrationBinding) {
        val parameter = Parameters(
            firstName = binding.etName.text.toString().trim(),
            lastName = "Hello",
            email = binding.etEmail.text.toString().trim(),
            number = binding.etMobileNo.text.toString().trim(),
            password = binding.etPassword.text.toString().trim()
        )
        (activity as AuthActivity).authViewModel.doRegistration(
            getHeaderMap("", false),
            Gson().toJson(parameter)
        )
    }

    private fun observeResponse() {
        (activity as AuthActivity).authViewModel.registrationResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()
                    showDialog(response.data?.message.toString(), true)
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(response.message.toString(), false)
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun showDialog(message: String, status: Boolean) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                if (status) {
                    binding.root.findNavController().popBackStack()
                }
            }
            .show()
        if (!status) {
            clearFields(binding)
        }
    }

    private fun clearFields(binding: FragmentRegistrationBinding) {
        binding.etName.setText("")
        binding.etEmail.setText("")
        binding.etMobileNo.setText("")
        binding.etAddress.setText("")
        binding.etPassword.setText("")
        binding.etConfirmPassword.setText("")
    }

    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.already_have_an_account_login))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController()
                    .navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment())
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
            tvAlreadyHaveAnAccount.movementMethod = LinkMovementMethod.getInstance()
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
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_name)
                )
                return false
            } else if (etEmail.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_email)
                )
                return false
            } else if (!isValidEmail(etEmail.text.toString().trim())
            ) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_valid_email)
                )
                return false
            } else if (etMobileNo.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_mobile_number)
                )
                return false
            } else if (etMobileNo.text.toString().trim().length < 10) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_valid_mobile_number)
                )
                return false
            } else if (etAddress.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_address)
                )
                return false
            } else if (etPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_password)
                )
                return false
            } else if (!isValidPassword(etPassword.text.toString().trim())) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_password_requirenments)
                )
                return false
            } else if (etConfirmPassword.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_enter_confirm_password)
                )
                return false
            } else if (etPassword.text?.trim().toString() != etConfirmPassword.text?.trim()
                    .toString()
            ) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.val_msg_password_not_matched)
                )
                return false
            } else if (!requireActivity().isConnected) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollviewSignup,
                    getString(R.string.message_no_internet_connection)
                )
                return false
            }
        }
        return true
    }

}