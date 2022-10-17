package com.specindia.ecommerce.ui.restautentdetaills.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.RowProductListItemBinding
import com.specindia.ecommerce.models.response.home.productsbyrestaurant.ProductsByRestaurantData
import com.specindia.ecommerce.ui.dashboard.home.HomeActivity
import com.specindia.ecommerce.util.isConnected
import com.specindia.ecommerce.util.showMaterialSnack
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
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        val existingRestaurantIdInCart =
                            activity.dataStoreViewModel.getExistingRestaurantIdFromCart()!!

                        if (existingRestaurantIdInCart != 0 && existingRestaurantIdInCart != product.restaurantId) {
                            btnAdd.visible(true)
                            clAddOrRemoveProduct.visible(false)
                            clearItemsFromCartAndAddTheNewOne(
                                product,
                                position,
                                btnAdd,
                                clAddOrRemoveProduct
                            )
                        } else {
                            btnAdd.visible(false)
                            clAddOrRemoveProduct.visible(true)
                            onProductItemClickListener.onAddButtonClick(product, position)
                        }
                    }
                }

                btnRemoveProduct.setOnClickListener {
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        onProductItemClickListener.onRemoveProductButtonClick(product, position)
                    }

                }

                btnAddProduct.setOnClickListener {
                    if (!activity.isConnected) {
                        showMaterialSnack(
                            activity,
                            it,
                            activity.getString(R.string.message_no_internet_connection)
                        )

                    } else {
                        onProductItemClickListener.onAddProductButtonClick(product, position)
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
                        onProductItemClickListener.onItemClick(product, position)
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    /**
     * Click to add,plus,minus button click
     * */
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

        fun onRemoveAllCartData(cartId: Int)
    }

    /**
    1. Remove All Cart by sending cartId as 0
    2. Then Add current Item to cart
    3. This will clear all items of Previous Restaurant from cart and add the new one for current Restaurant
     * */

    private fun clearItemsFromCartAndAddTheNewOne(
        product: ProductsByRestaurantData,
        position: Int,
        btnAdd: AppCompatButton,
        clAddOrRemoveProduct: ConstraintLayout,
    ) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.app_name))
            .setCancelable(false)
            .setMessage(activity.getString(R.string.msg_confirm_change_cart_item))
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->

                // This will remove all Previous Cart Items
                onProductItemClickListener.onRemoveAllCartData(cartId = 0)
                // This will save current Restaurant Id as 0. As we remove all items from Cart
                activity.dataStoreViewModel.saveExistingRestaurantIdOfCart(0)

                // Now We are adding New Product of New Restaurant and Update the UI
                Handler(Looper.getMainLooper()).postDelayed({
                    btnAdd.visible(false)
                    clAddOrRemoveProduct.visible(true)
                    onProductItemClickListener.onAddButtonClick(product, position)
                }, 500)

            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                // TODO : Code to execute
            }
            .show()
    }
}