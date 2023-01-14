package com.haroldcalayan.currencyexchanger.common.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.haroldcalayan.currencyexchanger.R

/**
 * Filename:    AlertDialogHelper
 * File Type:   Class
 * Description: Creates an Alert Dialog
 * Remarks:     None
 */
class AlertDialogHelper(var context: Context) {

    var alertDialogHelperTwoButtonsListener: AlertDialogHelperTwoButtonsListener? = null

    interface AlertDialogHelperTwoButtonsListener {
        fun onPositiveButtonClicked()
    }

    fun displayAlertDialogTwoButtons(
        title: String, message: String,
        textPositiveButton: String
    ) {
        context.let {
            val builder = MaterialAlertDialogBuilder(context, R.style.MyDialogTheme)
            builder.setMessage(message)
                .setTitle(title)
            builder.setNeutralButton(textPositiveButton) { _, _ ->
                alertDialogHelperTwoButtonsListener?.onPositiveButtonClicked()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }
}
