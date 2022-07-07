package com.specindia.ecommerce.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.ui.activity.OnBoardingActivity
import kotlinx.android.synthetic.main.row_on_boarding_item.view.*

class OnBoardingViewPagerAdapter(
    private val context: Context,
    private val array: List<OnBoardingActivity.OnBoardingData>
) : RecyclerView.Adapter<OnBoardingViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_on_boarding_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val currentData = array[position]
        holder.itemView.ivOnBoardingImage.setImageResource(currentData.imageId)
        holder.itemView.tvOnBoardingTitle.text =context.getString(currentData.title)
        holder.itemView.tvOnBoardingDescription.text = context.getString(currentData.description)
    }

    override fun getItemCount(): Int {
        return array.size
    }
}