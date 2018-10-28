package com.example.z003b2z.twodew.android.extensions

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt

fun Activity.changeStatusBarColor(@ColorInt color: Int, lightStatusBar: Boolean) {
    this.window?.let { win ->
        win.statusBarColor = color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = win.decorView.systemUiVisibility
            flags = if (lightStatusBar) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            win.decorView.systemUiVisibility = flags
        }
    }
}