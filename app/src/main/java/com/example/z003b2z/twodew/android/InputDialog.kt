package com.example.z003b2z.twodew.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.z003b2z.twodew.R
import kotlinx.android.synthetic.main.custom_input_dialog.settingsInputEditText
import kotlinx.android.synthetic.main.custom_input_dialog.view.settingsInputEditText
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.z003b2z.twodew.db.entity.Type
import kotlinx.android.synthetic.main.custom_input_dialog.settingsInputSpinner
import android.text.InputType
import kotlinx.android.synthetic.main.custom_input_dialog.view.settingsInputSpinner

@SuppressLint("InflateParams")
class InputDialog(context: Context, val hint: String, val type: Type, val listener: (input: String?) -> Unit) : AlertDialog(context) {

  init {
    val view = LayoutInflater.from(context).inflate(R.layout.custom_input_dialog, null)
    view.settingsInputEditText.hint = hint.plus("?")
    setView(view)

    setCancelButton()
    setPositiveButton(view)

    if(type == Type.WHEN) {
      view.settingsInputSpinner.visibility = View.VISIBLE
      view.settingsInputEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_PHONE
    }
  }

  private fun setPositiveButton(view: View) {
    setButton(AlertDialog.BUTTON_POSITIVE, "Add") { _, _ ->
      val text = if(type == Type.WHEN) {
        //TODO JOE DO VALIDATION ON WHEN INPUT
        view.settingsInputEditText.text.toString().plus(" ").plus(settingsInputSpinner.selectedItem.toString())
      } else {
        view.settingsInputEditText.text.toString()
      }
      listener(text)
      dismiss()
    }
  }

  private fun setCancelButton() {
    setCancelable(true)
    setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
      listener(null)
      dismiss()
    }
    setOnCancelListener {
      listener(null)
      dismiss()
    }
  }
}