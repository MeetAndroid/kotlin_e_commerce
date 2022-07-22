package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.databinding.RowCategoryListItemBinding
import com.specindia.ecommerce.models.response.home.Category

class CategoryListAdapter(private val arrayList: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {

    inner class CategoryListViewHolder(val binding: RowCategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding =
            RowCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val category = arrayList[position]

        with(holder) {
            with(binding) {
                Glide.with(itemView).load(category.image).into(ivCategory)
                tvCategoryName.text = category.name
            }

            itemView.setOnClickListener {
                onItemClickListener?.let { it(category) }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private var onItemClickListener: ((Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }
}