package com.specindia.ecommerce.ui.tutorial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spec.spec_ecommerce.databinding.RowOnBoardingItemBinding
import com.specindia.ecommerce.ui.tutorial.OnBoardingActivity

class OnBoardingViewPagerAdapter(
    private val context: Context,
    private val array: List<OnBoardingActivity.OnBoardingData>
) : RecyclerView.Adapter<OnBoardingViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: RowOnBoardingItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding =
            RowOnBoardingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val currentData = array[position]
        with(holder) {
            with(binding) {
                ivOnBoardingImage.setImageResource(currentData.imageId)
                tvOnBoardingTitle.text = context.getString(currentData.title)
                tvOnBoardingDescription.text = context.getString(currentData.description)
            }
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }
}