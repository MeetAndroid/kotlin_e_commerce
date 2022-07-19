package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.databinding.RowPopularRestaurantsBinding
import com.specindia.ecommerce.models.response.home.PopularRestaurent

class PopularRestaurantsAdapter :
    RecyclerView.Adapter<PopularRestaurantsAdapter.RestaurantsViewHolder>() {
    inner class RestaurantsViewHolder(val binding: RowPopularRestaurantsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<PopularRestaurent>() {
        override fun areItemsTheSame(
            oldItem: PopularRestaurent,
            newItem: PopularRestaurent
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PopularRestaurent,
            newItem: PopularRestaurent
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantsViewHolder {
        val binding =
            RowPopularRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantsViewHolder, position: Int) {
        val restaurant = differ.currentList[position]

        with(holder) {
            with(binding) {
                Glide.with(itemView).load(restaurant.imageUrl).into(ivRestaurant)
            }

            itemView.setOnClickListener {
                onItemClickListener?.let { it(restaurant) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((PopularRestaurent) -> Unit)? = null

    fun setOnItemClickListener(listener: (PopularRestaurent) -> Unit) {
        onItemClickListener = listener
    }
}