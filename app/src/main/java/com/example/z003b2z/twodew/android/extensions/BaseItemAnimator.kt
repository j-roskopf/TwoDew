package com.example.z003b2z.twodew.android.extensions

import jp.wasabeef.recyclerview.animators.BaseItemAnimator

fun BaseItemAnimator.setDuration(): BaseItemAnimator {
  return apply {
    addDuration = 100L
    changeDuration = 100L
    moveDuration = 100L
    removeDuration = 100L
  }
}