package com.specindia.ecommerce.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ActivityAutofitGridViewBinding
import com.specindia.ecommerce.ui.adapters.NumberedAdapter
import com.specindia.ecommerce.util.MarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoFitGridLayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAutofitGridViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutofitGridViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val arrayList = ArrayList<String>()
        for (i in 0..25) {
            arrayList.add("$i")
        }
        Log.d("arrayList", arrayList.size.toString())
        binding.apply {

            autoFitRecyclerView.addItemDecoration(MarginDecoration(resources.getDimensionPixelSize(R.dimen.item_margin),true))
            autoFitRecyclerView.hasFixedSize()
            autoFitRecyclerView.adapter = NumberedAdapter(arrayList)
        }
    }

}