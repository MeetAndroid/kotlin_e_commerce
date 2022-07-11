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
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentRegistrationBinding
import com.specindia.ecommerce.util.showShortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        setSpannableText()
        return binding.root
    }

    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.already_have_an_account_login))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController().popBackStack()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick(view)
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnRegister.setOnClickListener {
                view.findNavController().popBackStack()
            }

            btnFacebook.setOnClickListener {
                requireActivity().showShortToast("Coming soon...")
            }

            btnGooglePlus.setOnClickListener {
                requireActivity().showShortToast("Coming soon...")
            }
        }
    }

}