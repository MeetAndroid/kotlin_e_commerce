package com.specindia.ecommerce.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowShippingAddressBinding
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListData
import com.specindia.ecommerce.ui.activity.HomeActivity

class ShippingAddressAdapter(
    private val arrayList: ArrayList<GetAddressListData>,
    private val onShippingAddressClickListener: OnShippingAddressClickListener,
    private val context: HomeActivity,
) :
    RecyclerView.Adapter<ShippingAddressAdapter.ShippingAddressViewHolder>() {

    inner class ShippingAddressViewHolder(val binding: RowShippingAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingAddressViewHolder {
        val binding =
            RowShippingAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShippingAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShippingAddressViewHolder, position: Int) {
        val shippingAddress = arrayList[position]

        with(holder) {
            with(binding) {
                shippingAddress.apply {
                    if (primary) {
                        cvAddress.setCardBackgroundColor(ContextCompat.getColor(context,
                            R.color.red_with_alpha))
                        tvAddress.setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        cvAddress.setCardBackgroundColor(Color.WHITE)
                        tvAddress.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
                    }
                    tvAddressType.text = addressType
                    tvAddress.text = firstLine.plus(secondLine).plus(thirdLine)
                }
                itemView.setOnClickListener {
                    onShippingAddressClickListener.onItemClick(shippingAddress, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnShippingAddressClickListener {
        fun onItemClick(data: GetAddressListData, position: Int)
    }
}