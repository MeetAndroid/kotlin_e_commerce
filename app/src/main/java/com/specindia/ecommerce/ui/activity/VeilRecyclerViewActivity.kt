package com.specindia.ecommerce.ui.activity


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.spec.spec_ecommerce.R
import com.spec.spec_ecommerce.databinding.ActivityVeilBinding
import com.specindia.ecommerce.models.Profile
import com.specindia.ecommerce.ui.adapters.VeilAdapter
import com.specindia.ecommerce.util.ListItemUtils
import com.specindia.ecommerce.util.MarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VeilRecyclerViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVeilBinding
    private lateinit var veilAdapter: VeilAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVeilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileList: ArrayList<Profile> = ArrayList()
        profileList.addAll(ListItemUtils.getProfiles(this@VeilRecyclerViewActivity))
        profileList.trimToSize()

        veilAdapter = VeilAdapter(profileList)

        binding.veilRecyclerView.run {
            setVeilLayout(R.layout.item_profile)
            setAdapter(veilAdapter)
            binding.veilRecyclerView.getRecyclerView().addItemDecoration(MarginDecoration(resources.getDimensionPixelSize(R.dimen.item_margin),false))

//            setLayoutManager(GridLayoutManager(this@VeilRecyclerViewActivity, 2))

            setLayoutManager(LinearLayoutManager(this@VeilRecyclerViewActivity,
                LinearLayoutManager.HORIZONTAL,
                false))
//            setLayoutManager(LinearLayoutManager(this@VeilRecyclerViewActivity))
            addVeiledItems(15)
        }

        // delay-auto-unveil
        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.veilRecyclerView.unVeil()
            },
            3000
        )

    }
}