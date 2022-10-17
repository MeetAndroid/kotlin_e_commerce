package com.specindia.ecommerce.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.spec.spec_ecommerce.databinding.FragmentSearchBinding
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.models.response.AuthResponseData
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.ui.search.adapter.SearchListAdapter
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.getHeaderMap

/**
 * This fragment show to list of search whole product and restaurant as pr keyword type and call api to show list
 */

class SearchFragment : Fragment(), SearchListAdapter.OnSearchItemClickListener {
    private lateinit var data: AuthResponseData
    private lateinit var binding: FragmentSearchBinding
    private var searchList: ArrayList<ProductsByRestaurantData>? = null
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

        searchList?.let { setupUI(it) }
        setObserver()

        //type your word to search
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

    }

    //setup header component hide and show as per requirement
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

    //Observe by search product response
    private fun setObserver() {
        (activity as HomeActivity).homeViewModel.searchResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    searchList = ArrayList()
                    response.data?.data?.let { searchList!!.addAll(it) }
                    searchList.let {
                        if (it != null) {
                            setupUI(it)
                        }
                    }
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    // set to data in recyclerview
    private fun setupUI(searchList: ArrayList<ProductsByRestaurantData>) {
        searchListAdapter = SearchListAdapter(searchList, this, (activity as HomeActivity))
        binding.rvSearch.adapter = searchListAdapter
    }

    // click any item to redirect product details fragment and pass parameter (productid,tag,bundle)
    override fun onSearchItemClick(data: ProductsByRestaurantData, position: Int) {
        val bundle = Gson().toJson(data)
        view?.findNavController()
            ?.navigate(
                SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(
                    data.productId,
                    Constants.SEARCH,
                    bundle
                )
            )
    }

    override fun onResume() {
        super.onResume()
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
}