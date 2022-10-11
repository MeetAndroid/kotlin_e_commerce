package com.specindia.ecommerce.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.spec.spec_ecommerce.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAuthBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick(view)
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnLogin.setOnClickListener {
                view.findNavController()
                    .navigate(AuthFragmentDirections.actionAuthFragmentToLoginFragment())
            }
            btnRegister.setOnClickListener {
                view.findNavController()
                    .navigate(AuthFragmentDirections.actionAuthFragmentToWelcomeFragment())
            }
        }
    }
}