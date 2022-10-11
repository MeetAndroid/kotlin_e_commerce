package com.specindia.ecommerce.ui.dashboard.menu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.RowMenuListItemBinding
import com.specindia.ecommerce.models.response.menulist.Menu

class MenuListAdapter(private val arrayList: ArrayList<Menu>) :
    RecyclerView.Adapter<MenuListAdapter.MenuListViewHolder>() {

    inner class MenuListViewHolder(val binding: RowMenuListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuListViewHolder {
        val binding =
            RowMenuListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuListViewHolder, position: Int) {
        val menuData = arrayList[position]
        with(holder) {
            with(binding) {
                Glide.with(itemView)
                    .load(menuData.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivMenu)
                tvMenu.text = menuData.name

                itemView.setOnClickListener {
                    onItemClickListener?.let { it(menuData) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private var onItemClickListener: ((Menu) -> Unit)? = null

    fun setOnItemClickListener(listener: (Menu) -> Unit) {
        onItemClickListener = listener
    }
}