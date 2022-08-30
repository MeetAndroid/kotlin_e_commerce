package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowOrderDetailsBinding
import com.specindia.ecommerce.models.response.home.order.CartsItem

class OrderListAdapter(
    private val arrayList: ArrayList<CartsItem>,
) :
    RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder>() {

    inner class OrderListViewHolder(val binding: RowOrderDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val binding =
            RowOrderDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val cartData = arrayList[position]
        val product = cartData.product

        with(holder) {
            with(binding) {

                Glide.with(itemView)
                    .load(product?.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivProductImage)
                tvProductName.text = product?.productName
                tvProductPrice.text = product?.price.toString()
                tvOrderCount.text = cartData.quantity

            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}