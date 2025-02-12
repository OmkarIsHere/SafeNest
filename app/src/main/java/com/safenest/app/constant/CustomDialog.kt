package com.safenest.app.constant

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.safenest.app.R

class CustomDialog(private val context: Context) {

    private var dialog: Dialog? = null

    fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String = "YES",
        negativeButtonText: String = "Cancel",
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {

        dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
        dialog?.setContentView(view)

        val titleTextView: TextView = view.findViewById(R.id.txtDialogTitle)
        val messageTextView: TextView = view.findViewById(R.id.txtDialogContent)
        val positiveButton: Button = view.findViewById(R.id.btnPositive)
        val negativeButton: Button = view.findViewById(R.id.btnNegative)

        titleTextView.text = title
        messageTextView.text = message
        positiveButton.text = positiveButtonText
        negativeButton.text = negativeButtonText

        positiveButton.setOnClickListener {
            onPositiveClick?.invoke()
            dismissDialog()
        }

        negativeButton.setOnClickListener {
            onNegativeClick?.invoke()
            dismissDialog()
        }

        dialog?.show()
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }
}