package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.databinding.FragmentForgotPasswordBinding
import com.specindia.ecommerce.util.showMaterialSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_forgot_password.*

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
        binding.btnForgotPassword.setOnClickListener {
            if (binding.etForgotPasswordEmail.text.toString().trim().isEmpty()) {
                showMaterialSnack(requireContext(), nestedScrollview, "Please enter email")
            }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etForgotPasswordEmail.text.toString().trim())
                    .matches()){
                showMaterialSnack(requireContext(), nestedScrollview, "Please enter valid email")
            }
            else{
                view.findNavController().popBackStack()
            }
        }
    }

}