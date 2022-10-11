package com.specindia.ecommerce.util.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.spec.spec_ecommerce.R

class CustomProgressDialog(context: Context) : BaseDialogHelper() {

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.layout_progress_bar, null)
    }
    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

}