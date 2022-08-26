package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowCartListItemBinding
import com.specindia.ecommerce.models.response.cart.getcart.GetCartData

class CartListAdapter(
    private val arrayList: ArrayList<GetCartData>,
    private val onCartItemClickListener: OnCartItemClickListener,
) :
    RecyclerView.Adapter<CartListAdapter.CartListViewHolder>() {

    inner class CartListViewHolder(val binding: RowCartListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        val binding =
            RowCartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        val cartData = arrayList[position]
        val product = cartData.product

        with(holder) {
            with(binding) {

                Glide.with(itemView)
                    .load(product.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivProductImage)

                tvProductName.text = product.productName
                tvProductPrice.text = product.price.toString()
                tvQty.text = cartData.quantity.toString()

                btnRemoveProduct.setOnClickListener {
                    onCartItemClickListener.onRemoveProductButtonClick(cartData, position)
                }

                btnAddProduct.setOnClickListener {
                    onCartItemClickListener.onAddProductButtonClick(cartData, position)
                }

                itemView.setOnClickListener {
                    onCartItemClickListener.onItemClick(cartData, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnCartItemClickListener {
        fun onItemClick(data: GetCartData, position: Int)
        fun onAddProductButtonClick(data: GetCartData, position: Int)
        fun onRemoveProductButtonClick(data: GetCartData, position: Int)
    }
}