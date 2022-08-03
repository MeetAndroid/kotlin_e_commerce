package com.specindia.ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ItemProfileBinding
import com.specindia.ecommerce.models.Profile

class VeilAdapter(
    private val profiles: ArrayList<Profile>,
) : RecyclerView.Adapter<VeilAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VeilAdapter.ProfileViewHolder {
        val binding =
            ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }


    inner class ProfileViewHolder(val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VeilAdapter.ProfileViewHolder, position: Int) {
        val profile = profiles[position]

        with(holder) {
            with(binding) {
                name.text = profile.name
                content.text = profile.content

                Glide.with(itemView)
                    .load(profile.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(binding.profile)
            }
        }

    }

    override fun getItemCount(): Int {
        return profiles.size
    }
}
