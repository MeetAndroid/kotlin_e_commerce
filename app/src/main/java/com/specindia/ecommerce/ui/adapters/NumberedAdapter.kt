package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spec.spec_ecommerce.databinding.RowAutoFitItemBinding

class NumberedAdapter(
    private val labels: ArrayList<String>,
) : RecyclerView.Adapter<NumberedAdapter.TextViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NumberedAdapter.TextViewHolder {
        val binding =
            RowAutoFitItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TextViewHolder(binding)
    }

    inner class TextViewHolder(val binding: RowAutoFitItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: NumberedAdapter.TextViewHolder, position: Int) {
        val label = labels[position]

        with(holder) {
            with(binding) {
                textView.text = label
            }
        }

    }

    override fun getItemCount(): Int {
        return labels.size
    }
}
