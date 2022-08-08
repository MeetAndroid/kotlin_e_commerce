package com.specindia.ecommerce.models

data class ProductWithTotalQty(val productId:String,val totalQty:String,val cartId:String,val positionInAdapter:Int,val isCartExist:Boolean)