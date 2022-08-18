package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.databinding.RowAddAddressBinding

class AddAddressAdapter(
    private val arrayList: ArrayList<String>,
) : RecyclerView.Adapter<AddAddressAdapter.AddAddressViewHolder>() {

    inner class AddAddressViewHolder(val binding: RowAddAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAddressViewHolder {
        val binding =
            RowAddAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddAddressViewHolder, position: Int) {
        val addressLine = arrayList[position]

        with(holder) {
            with(binding) {
                tvAddressLine.text = addressLine
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}