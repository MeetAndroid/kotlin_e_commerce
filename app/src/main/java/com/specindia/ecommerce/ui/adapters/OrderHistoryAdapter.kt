package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowOrderHistoryBinding
import com.specindia.ecommerce.models.response.home.orderlist.OrderData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants
import com.specindia.ecommerce.util.dateFormat
import com.specindia.ecommerce.util.isConnected
import com.specindia.ecommerce.util.showMaterialSnack

class OrderHistoryAdapter(
    private val arrayList: ArrayList<OrderData>,
    private val onItemOrderHistoryItemClick: OnOrderHistoryItemClickListener,
    private val activity: HomeActivity,
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
        val order = arrayList[position]

        with(holder) {
            with(binding) {
                tvOrderId.text =
                    itemView.resources.getString(R.string.order_id, order.id.toString())
                tvOrderAmount.text = itemView.resources.getString(R.string.amount, order.total)
                tvOrderDate.text = itemView.resources.getString(R.string.date,
                    order.createdAt?.let {
                        dateFormat(it,
                            Constants.DateFormat.INPUT_FORMAT,
                            Constants.DateFormat.OUTPUT_FORMAT)
                    })

                itemView.setOnClickListener {
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        order.id?.let { it1 -> onItemOrderHistoryItemClick.onItemClick(it1) }
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnOrderHistoryItemClickListener {
        fun onItemClick(orderId: Int)
    }
}