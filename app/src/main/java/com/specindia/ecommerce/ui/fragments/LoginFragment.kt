package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentLoginBinding
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.startNewActivity

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
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
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnLogin.setOnClickListener {
                requireActivity().startNewActivity(HomeActivity::class.java)
            }

            btnForgotPassword.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
    }

}