package com.specindia.ecommerce.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.RowRecentProductBinding
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData

class RecentProductsAdapter(private val arrayList: ArrayList<ProductsByRestaurantData>) :
    RecyclerView.Adapter<RecentProductsAdapter.RecentViewHolder>() {

    inner class RecentViewHolder(val binding: RowRecentProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding =
            RowRecentProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val dishes = arrayList[position]

        with(holder) {
            with(binding) {
                Glide.with(itemView)
                    .load(dishes.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivRecentProduct)
                tvRecentName.text = dishes.productName
            }

            itemView.setOnClickListener {
                onItemClickListener?.let { it(dishes) }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private var onItemClickListener: ((ProductsByRestaurantData) -> Unit)? = null

    fun setOnItemClickListener(listener: (ProductsByRestaurantData) -> Unit) {
        onItemClickListener = listener
    }
}