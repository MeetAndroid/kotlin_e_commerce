package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.specindia.ecommerce.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeaderItemClick()
    }

    private fun setUpHeaderItemClick() {
        with(binding) {
            searchView.apply {
                setIconifiedByDefault(true)
                isFocusable = true
                isIconified = false
                clearFocus()
                requestFocusFromTouch()
            }
            ivBack.setOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }
}