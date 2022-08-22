package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.databinding.RowShippingAddressBinding
import com.specindia.ecommerce.models.response.home.getaddress.GetAddressListData

class ShippingAddressAdapter(
    private val arrayList: ArrayList<GetAddressListData>,
    private val onShippingAddressClickListener: OnShippingAddressClickListener,
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

                tvAddressType.text = shippingAddress.addressType
                tvAddress.text = shippingAddress.firstLine.plus(shippingAddress.secondLine)
                    .plus(shippingAddress.thirdLine)

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