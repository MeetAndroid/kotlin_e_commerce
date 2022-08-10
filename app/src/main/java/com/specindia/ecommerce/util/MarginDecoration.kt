package com.specindia.ecommerce.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginDecoration(private val spaceHeight: Int, private val isGridLayout: Boolean) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State,
    ) {
        with(outRect) {
            if (!isGridLayout) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight
                }
            }
            left = spaceHeight
            right = spaceHeight
            bottom = spaceHeight
        }
    }
}