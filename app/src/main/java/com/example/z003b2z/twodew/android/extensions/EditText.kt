package com.example.z003b2z.twodew.android.extensions

import android.widget.EditText

fun EditText.focus() {
    isEnabled = true
    requestFocus()
    setSelection(length())
}