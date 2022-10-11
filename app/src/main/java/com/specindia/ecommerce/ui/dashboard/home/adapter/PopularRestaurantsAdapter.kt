package com.specindia.ecommerce.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.RowPopularRestaurantsBinding
import com.specindia.ecommerce.models.response.home.PopularRestaurent

class PopularRestaurantsAdapter(private val arrayList: ArrayList<PopularRestaurent>) :
    RecyclerView.Adapter<PopularRestaurantsAdapter.RestaurantsViewHolder>() {

    inner class RestaurantsViewHolder(val binding: RowPopularRestaurantsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantsViewHolder {
        val binding =
            RowPopularRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantsViewHolder, position: Int) {
        val restaurant = arrayList[position]

        with(holder) {
            with(binding) {
                Glide.with(itemView)
                    .load(restaurant.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivRestaurant)

                tvRestaurantName.text = restaurant.name
                tvRestaurantAddress.text = restaurant.address

                if (position % 2 == 0) {
                    totalRatings.text = "2.4"
                    ratings.rating = 2.4f
                } else {
                    totalRatings.text = "3.6"
                    ratings.rating = 3.6f
                }

            }

            itemView.setOnClickListener {
                onItemClickListener?.let { it(restaurant) }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private var onItemClickListener: ((PopularRestaurent) -> Unit)? = null

    fun setOnItemClickListener(listener: (PopularRestaurent) -> Unit) {
        onItemClickListener = listener
    }
}