package com.specindia.ecommerce.ui.viewall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.RowViewAllBinding
import com.specindia.ecommerce.models.response.home.product.ViewAllItems

class ViewAllAdapter(
    private val arrayList: ArrayList<ViewAllItems>,
    private val onViewAllClickListener: OnViewAllClickListener,
) :
    RecyclerView.Adapter<ViewAllAdapter.ProductListViewHolder>() {

    inner class ProductListViewHolder(val binding: RowViewAllBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding =
            RowViewAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val product = arrayList[position]

        with(holder) {
            with(binding) {

                Glide.with(itemView)
                    .load(product.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivProductImage)
                tvProductName.text = product.productName
                tvProductPrice.text = product.price.toString()
            }

            itemView.setOnClickListener {
                onViewAllClickListener.onItemClick(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnViewAllClickListener {
        fun onItemClick(position: Int)
    }
}