package com.specindia.ecommerce.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowProductListItemBinding
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.util.visible

class ProductListAdapter(private val arrayList: ArrayList<ProductsByRestaurantData>) :
    RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {
    var showShimmer: Boolean = true

    inner class ProductListViewHolder(val binding: RowProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding =
            RowProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val product = arrayList[position]

        with(holder) {
            with(binding) {
                if (showShimmer) {
                    shimmerLayout.startShimmer()
                } else {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.setShimmer(null)
                    tvProductName.background = null
                    tvProductPrice.background = null

                    Glide.with(itemView)
                        .load(product.productImage)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(android.R.drawable.ic_dialog_alert)
                        .into(ivProductImage)
                    tvProductName.text = product.productName
                    tvProductPrice.text = product.price.toString()
                    tvQty.text = product.totalQty.toString()

                    btnAdd.setOnClickListener {
                        btnAdd.visible(false)
                        clAddOrRemoveProduct.visible(true)
                    }

                    btnRemoveProduct.setOnClickListener {
                        Log.d("Adapter", "Removed")
                        onRemoveButtonClickListener?.let { Pair(product, position) }
                    }

                    btnAddProduct.setOnClickListener {
                        Log.d("Adapter", "Added")
                        onAddButtonClickListener?.let { Pair(product, position) }
                    }

                }

                itemView.setOnClickListener {
                    onItemClickListener?.let { it(product) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private var onItemClickListener: ((ProductsByRestaurantData) -> Unit)? = null

    private var onAddButtonClickListener: ((data: ProductsByRestaurantData, position: Int) -> Unit)? =
        null

    private var onRemoveButtonClickListener: ((data: ProductsByRestaurantData, position: Int) -> Unit)? =
        null

    fun setOnItemClickListener(listener: (ProductsByRestaurantData) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnRemoveButtonClickListener(
        listener: (data: ProductsByRestaurantData, position: Int) -> Unit,
    ) {
        onRemoveButtonClickListener = listener
    }

    fun setOnAddButtonClickListener(
        listener: (data: ProductsByRestaurantData, position: Int) -> Unit,
    ) {
        onAddButtonClickListener = listener
    }

}