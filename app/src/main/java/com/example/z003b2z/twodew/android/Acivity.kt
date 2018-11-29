package com.example.z003b2z.twodew.android

import android.app.Activity
import android.content.Intent

const val RESULT_DATA_CHANGED = 153

fun Activity.startActivityWithAnimation(intent: Intent) {
  startActivityForResult(intent, RESULT_DATA_CHANGED)
  overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
}