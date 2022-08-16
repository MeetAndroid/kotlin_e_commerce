package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowProductListItemBinding
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.showShortToast
import com.specindia.ecommerce.util.visible

class ProductListAdapter(
    private val arrayList: ArrayList<ProductsByRestaurantData>,
    private val onProductItemClickListener: OnProductItemClickListener,
    private val activity: HomeActivity,
) :
    RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    inner class ProductListViewHolder(val binding: RowProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding =
            RowProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val product = arrayList[position]

        with(holder) {
            with(binding) {

                Glide.with(itemView)
                    .load(product.productImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(ivProductImage)

                tvProductName.text = product.productName
                tvProductPrice.text = product.price.toString()
                tvQty.text = product.totalQty.toString()

                if (product.totalQty > 0) {
                    btnAdd.visible(false)
                    clAddOrRemoveProduct.visible(true)
                } else {
                    btnAdd.visible(true)
                    clAddOrRemoveProduct.visible(false)
                }

                btnAdd.setOnClickListener {
                    val existingRestaurantIdInCart =
                        activity.dataStoreViewModel.getExistingRestaurantIdFromCart()!!

                    if (existingRestaurantIdInCart != 0 && existingRestaurantIdInCart != product.restaurantId) {
                        activity.showShortToast(activity.getString(R.string.msg_product_of_another_restaurant_is_already_exist))
                        btnAdd.visible(true)
                        clAddOrRemoveProduct.visible(false)
                    } else {
                        btnAdd.visible(false)
                        clAddOrRemoveProduct.visible(true)
                        onProductItemClickListener.onAddButtonClick(product, position)
                    }

                }

                btnRemoveProduct.setOnClickListener {
                    onProductItemClickListener.onRemoveProductButtonClick(product, position)
                }

                btnAddProduct.setOnClickListener {
                    onProductItemClickListener.onAddProductButtonClick(product, position)
                }

                itemView.setOnClickListener {
                    onProductItemClickListener.onItemClick(product, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface OnProductItemClickListener {
        fun onItemClick(data: ProductsByRestaurantData, position: Int)
        fun onAddButtonClick(data: ProductsByRestaurantData, position: Int)
        fun onAddProductButtonClick(
            data: ProductsByRestaurantData,
            position: Int,
        )

        fun onRemoveProductButtonClick(
            data: ProductsByRestaurantData,
            position: Int,
        )
    }
}