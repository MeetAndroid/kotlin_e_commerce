package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowProductListItemBinding
import com.specindia.ecommerce.databinding.RowSearchListItemBinding
import com.specindia.ecommerce.models.response.home.SearchItem
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.util.visible

class SearchListAdapter(
    private val arrayList: ArrayList<SearchItem>,
    private val onSearchItemClickListener: OnSearchItemClickListener
) :
    RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    inner class SearchListViewHolder(val binding: RowSearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val binding =
            RowSearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val product = arrayList[position]

        with(holder) {
            with(binding) {

                Glide.with(itemView)
                    .load(product.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivProductImage)

                tvProductName.text = product.productName
                tvProductPrice.text = product.price

                itemView.setOnClickListener {
                    onSearchItemClickListener.onSearchItemClick(product, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnSearchItemClickListener {
        fun onSearchItemClick(data: SearchItem, position: Int)
    }
}