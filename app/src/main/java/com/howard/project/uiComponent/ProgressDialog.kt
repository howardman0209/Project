package com.howard.project.uiComponent

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.howard.project.databinding.ProgressDialogBinding
import com.howard.project.R

class ProgressDialog(context: Context) : MaterialAlertDialogBuilder(context) {

    init {
        val layoutBinding: ProgressDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.progress_dialog, null, false)
        setView(layoutBinding.root)
        setCancelable(false)
        background = ColorDrawable(Color.TRANSPARENT)
    }

    override fun create(): AlertDialog {
        val dialog = super.create()
        dialog.window?.apply {
            setDimAmount(.12f)
            // for full screen dialog need to set the decorView padding to be 0
            // decorView.setPadding(0, 0, 0, 0)
        }
        return dialog
    }

}