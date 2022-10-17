package com.specindia.ecommerce.ui.authentication.login

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
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.FragmentLoginBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.ui.authentication.AuthActivity
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var customProgressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick(view)
        setSpannableText()
        startEditTextSpace()
        setUpProgressDialog()

        observeResponse()
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnLogin.setOnClickListener {
                if (isEmpty()) {
                    customProgressDialog.show()
                    callLoginApi(binding)

                }
            }

            btnForgotPassword.setOnClickListener {
                view.findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            }
        }
    }

    // call login api
    private fun callLoginApi(binding: FragmentLoginBinding) {
        val parameter = Parameters(
            email = binding.etLoginEmail.text.toString().trim(),
            password = binding.etPassword.text.toString().trim()
        )

        (activity as AuthActivity).authViewModel.doLogin(
            getHeaderMap("", false),
            Gson().toJson(parameter)
        )
    }

    // Observe login response
    private fun observeResponse() {
        (activity as AuthActivity).authViewModel.loginResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.dismiss()

                    //Save User response as a Json In Data Store
                    val data = Gson().toJson(response.data?.data)
                    CoroutineScope(Dispatchers.IO).launch {
                        (activity as AuthActivity).dataStoreRepository.putString(Constants.KEY_LOGGED_IN_USER_DATA, data)
                    }
                    requireActivity().showShortToast("Login Successfully ...")
                    requireActivity().startNewActivity(HomeActivity::class.java)
                    (activity as AuthActivity).dataStoreViewModel.saveUserLoggedIn(true)
                }
                is NetworkResult.Error -> {
                    customProgressDialog.dismiss()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
//                clearFields(binding)
            }
            .show()
    }

    // this function set to redirect registration
    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.don_t_have_an_account_sign_up))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
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

    // when user start to type space not set any space in edit text
    private fun startEditTextSpace() {
        with(binding) {
            etLoginEmail.emptyEditText(etLoginEmail)
            etPassword.emptyEditText(etPassword)
        }
    }

    // validation set
    private fun isEmpty(): Boolean {
        binding.apply {
            if (etLoginEmail.text.toString().trim().isEmpty()) {
                showMaterialSnack(
                    requireContext(),
                    nestedScrollview,
                    getString(R.string.val_msg_enter_email)
                )
                return false
            } else {
                if (!isValidEmail(etLoginEmail.text.toString().trim())) {
                    showMaterialSnack(
                        requireContext(),
                        nestedScrollview,
                        getString(R.string.val_msg_enter_valid_email)
                    )
                    return false
                } else if (etPassword.text.toString().trim().isEmpty()) {
                    showMaterialSnack(
                        requireContext(),
                        nestedScrollview,
                        getString(R.string.val_msg_enter_password)
                    )
                    return false
                } else if (!requireActivity().isConnected) {
                    showMaterialSnack(
                        requireContext(),
                        nestedScrollview,
                        getString(R.string.message_no_internet_connection)
                    )
                    return false
                }
            }
        }
        return true
    }
}