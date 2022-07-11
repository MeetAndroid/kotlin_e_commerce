package com.specindia.ecommerce.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
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
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnLogin.setOnClickListener {
                viewModel.saveUserLoggedIn(true)
                requireActivity().startNewActivity(HomeActivity::class.java)
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
                Log.e("DATA", "data")
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
}