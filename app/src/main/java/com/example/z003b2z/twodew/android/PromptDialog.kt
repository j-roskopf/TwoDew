package com.example.z003b2z.twodew.android

import android.content.DialogInterface
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import com.example.z003b2z.twodew.R
import android.view.ViewGroup
import android.widget.FrameLayout

interface ClickListener {
    fun clicked(text: String)
}

class PromptDialog(context: Context, title: Int, message: Int, private val clickListener: ClickListener) : AlertDialog.Builder(context) {
    private val input: EditText

    init {
        this.setTitle(title)
        this.setMessage(message)

        input = EditText(context)
        input.setSingleLine()

        val container = FrameLayout(context)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        params.leftMargin = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = context.resources.getDimensionPixelSize(R.dimen.dialog_margin) * 2
        input.layoutParams = params

        container.addView(input)
        this.setView(container)

        this.setPositiveButton(R.string.ok) { dialog: DialogInterface, which: Int ->
            onClick(dialog, which)
        }
        this.setNegativeButton(R.string.cancel) { dialog: DialogInterface, which: Int ->
            onClick(dialog, which)
        }
    }

    fun onClick(dialog: DialogInterface, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            clickListener.clicked(input.text.toString())
        } else {
            dialog.dismiss()
        }
    }
}