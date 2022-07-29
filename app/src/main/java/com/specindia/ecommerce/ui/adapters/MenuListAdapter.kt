package com.specindia.ecommerce.ui.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowMenuListItemBinding
import com.specindia.ecommerce.models.response.menulist.Menu

class MenuListAdapter(private val arrayList: ArrayList<Menu>) :
    RecyclerView.Adapter<MenuListAdapter.MenuListViewHolder>() {
    var showShimmer: Boolean = true

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
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        if (showShimmer) {
                            shimmerLayout.startShimmer()
                        } else {
                            shimmerLayout.stopShimmer()
                            shimmerLayout.setShimmer(null)
                            ivMenu.background = null
                            tvMenu.background = null

                            Glide.with(itemView)
                                .load(menuData.image)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(android.R.drawable.ic_dialog_alert)
                                .into(ivMenu)
                            tvMenu.text = menuData.name

                        }
                    }, 2000
                )


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