package com.haroldcalayan.currencyexchanger.common.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.haroldcalayan.currencyexchanger.R

/**
 * Filename:    ProgressDialogHelper
 * File Type:   Class
 * Description: Creates a ProgressDialog
 * Remarks:     None
 */
class ProgressDialogHelper {

    companion object {
        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }
    }
}
