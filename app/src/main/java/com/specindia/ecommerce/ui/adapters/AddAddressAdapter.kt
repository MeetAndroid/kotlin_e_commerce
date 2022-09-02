package com.specindia.ecommerce.ui.adapters

import android.graphics.Color
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.RowAddAddressBinding
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.fragments.AddAddressFragment
import com.specindia.ecommerce.util.editableDrawableText.DrawablePosition
import com.specindia.ecommerce.util.editableDrawableText.OnDrawableClickListener
import com.specindia.ecommerce.util.hideKeyboard
import com.specindia.ecommerce.util.setReadOnly
import com.specindia.ecommerce.util.showKeyboard

class AddAddressAdapter(
    private val arrayList: ArrayList<String>,
    private val activity: HomeActivity,
    private val fragment: AddAddressFragment,
) : RecyclerView.Adapter<AddAddressAdapter.AddAddressViewHolder>() {

    inner class AddAddressViewHolder(val binding: RowAddAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAddressViewHolder {
        val binding =
            RowAddAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddAddressViewHolder, position: Int) {
        val addressLine = arrayList[holder.adapterPosition]

        with(holder) {
            with(binding) {
                // Initially Add Edit Text are disable by default
                etAddressLine.setText(addressLine.trim())
                etAddressLine.setReadOnly(true, InputType.TYPE_NULL)
                etAddressLine.setTextColor(Color.GRAY)
                etAddressLine.setCompoundDrawablesWithIntrinsicBounds(null,
                    null,
                    ContextCompat.getDrawable(activity, R.drawable.ic_edit),
                    null)

                // On EditText Click we enable it
                etAddressLine.setOnClickListener {
                    etAddressLine.setReadOnly(false, InputType.TYPE_CLASS_TEXT)
                    etAddressLine.setTextColor(Color.BLACK)
                    etAddressLine.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        ContextCompat.getDrawable(activity, R.drawable.ic_done),
                        null)
                    etAddressLine.text?.length?.let { it1 -> etAddressLine.setSelection(it1) }
                    etAddressLine.showKeyboard()
                }

                // On Done button click we disable Edit Text and update value in arraylist, notify item
                etAddressLine.setDrawableClickListener(object : OnDrawableClickListener {
                    override fun onClick(target: DrawablePosition) {
                        if (target == DrawablePosition.RIGHT) {
                            if (etAddressLine.text.isNullOrEmpty()) {
                                fragment.confirmToDeleteAddress(activity,
                                    arrayList[holder.adapterPosition],
                                    holder.adapterPosition)
                            } else {
                                etAddressLine.setReadOnly(true, InputType.TYPE_NULL)
                                etAddressLine.setTextColor(Color.GRAY)
                                etAddressLine.setCompoundDrawablesWithIntrinsicBounds(null,
                                    null,
                                    ContextCompat.getDrawable(activity, R.drawable.ic_edit),
                                    null)

                                // Update the value and notify item changed
                                arrayList[holder.adapterPosition] = etAddressLine.text.toString()
                                notifyItemChanged(holder.adapterPosition)
                                etAddressLine.hideKeyboard()
                            }

                        }
                    }

                })
            }
        }
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount(): Int {
        return arrayList.size
    }

}