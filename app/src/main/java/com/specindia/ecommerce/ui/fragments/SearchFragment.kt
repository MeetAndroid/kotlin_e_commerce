package com.specindia.ecommerce.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentSearchBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.SearchItem
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.adapters.SearchListAdapter
import com.specindia.ecommerce.util.getHeaderMap


class SearchFragment : Fragment(), SearchListAdapter.OnSearchItemClickListener {
    private lateinit var data: AuthResponseData
    private lateinit var binding: FragmentSearchBinding
    private var searchList: ArrayList<SearchItem>? = null
    lateinit var searchListAdapter: SearchListAdapter

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

        val userData = (activity as HomeActivity).dataStoreViewModel.getLoggedInUserData()
        data = Gson().fromJson(userData, AuthResponseData::class.java)
        Log.e("TOKEN", data.token)

        searchList?.let { setupUI(it) }
        setObserver()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val parameter = newText?.let {
                    Parameters(
                        text = it,
                        pageNo = 1,
                        limit = 10
                    )
                }

                (activity as HomeActivity).homeViewModel.getSearch(
                    getHeaderMap(
                        data.token,
                        true
                    ),
                    Gson().toJson(parameter)
                )
                return false
            }
        })

        val parameter =
            Parameters(text = "", pageNo = 1, limit = 10)

        (activity as HomeActivity).homeViewModel.getSearch(
            getHeaderMap(
                data.token,
                true
            ),
            Gson().toJson(parameter)
        )
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

    private fun setObserver() {
        (activity as HomeActivity).homeViewModel.searchResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    searchList = ArrayList()
                    searchList = response.data?.data
                    searchList?.let { setupUI(it) }
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun setupUI(searchList: ArrayList<SearchItem>) {
        searchListAdapter = SearchListAdapter(searchList, this)
        binding.rvSearch.adapter = searchListAdapter
    }

    override fun onSearchItemClick(data: SearchItem, position: Int) {
        Log.e("ITEM-NAME :- ", data.productName)
    }
}