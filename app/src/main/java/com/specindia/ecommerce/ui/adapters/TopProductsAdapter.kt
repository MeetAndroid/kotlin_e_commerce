package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.databinding.RowTopProductBinding
import com.specindia.ecommerce.models.response.home.TopProduct

class TopProductsAdapter(private val arrayList: ArrayList<TopProduct>) :
    RecyclerView.Adapter<TopProductsAdapter.TopDishesViewHolder>() {

    inner class TopDishesViewHolder(val binding: RowTopProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopDishesViewHolder {
        val binding =
            RowTopProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopDishesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopDishesViewHolder, position: Int) {
        val dishes = arrayList[position]

        with(holder) {
            with(binding) {
                Glide.with(itemView).load(dishes.productImage).into(ivTopProduct)
                tvTopDishesName.text = dishes.productName
            }

            itemView.setOnClickListener {
                onItemClickListener?.let { it(dishes) }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private var onItemClickListener: ((TopProduct) -> Unit)? = null

    fun setOnItemClickListener(listener: (TopProduct) -> Unit) {
        onItemClickListener = listener
    }
}