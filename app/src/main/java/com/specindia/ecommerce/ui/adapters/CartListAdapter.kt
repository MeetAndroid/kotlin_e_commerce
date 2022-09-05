package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowCartListItemBinding
import com.specindia.ecommerce.models.response.cart.getcart.GetCartData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.isConnected
import com.specindia.ecommerce.util.showMaterialSnack

class CartListAdapter(
    private val arrayList: ArrayList<GetCartData>,
    private val onCartItemClickListener: OnCartItemClickListener,
    private val activity: HomeActivity,
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
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        onCartItemClickListener.onRemoveProductButtonClick(cartData, position)
                    }
                    onCartItemClickListener.onRemoveProductButtonClick(cartData, position)
                }

                btnAddProduct.setOnClickListener {
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        onCartItemClickListener.onAddProductButtonClick(cartData, position)
                    }

                }

                itemView.setOnClickListener {
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        onCartItemClickListener.onItemClick(cartData, position)
                    }
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