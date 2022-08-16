package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowOrderHistoryBinding
import com.specindia.ecommerce.models.response.home.orderlist.OrderData
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.dateFormat

class OrderHistoryAdapter(
    private val arrayList: ArrayList<OrderData>,
    private val onItemOrderHistoryItemClick: OrderHistoryAdapter.OnOrderHistoryItemClickListener,
) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderListViewHolder>() {

    inner class OrderListViewHolder(val binding: RowOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val binding =
            RowOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val product = arrayList[position]

        with(holder) {
            with(binding) {
                tvOrderId.text =
                    itemView.resources.getString(R.string.order_id, product.id.toString())
                tvOrderAmount.text = itemView.resources.getString(R.string.amount, product.total)
                tvOrderDate.text = itemView.resources.getString(R.string.date,
                    product.createdAt?.let {
                        dateFormat(it,
                            Constants.DateFormat.INPUT_FORMAT,
                            Constants.DateFormat.OUTPUT_FORMAT)
                    })

                itemView.setOnClickListener {
                    onItemOrderHistoryItemClick.onItemClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnOrderHistoryItemClickListener {
        fun onItemClick(position: Int)
    }
}