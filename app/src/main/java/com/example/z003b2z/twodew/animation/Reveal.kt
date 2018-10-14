package com.example.z003b2z.twodew.animation

import android.view.View
import android.view.ViewAnimationUtils


class Reveal {
    fun reveal(startView: View, baseView: View) {
        val x = startView.bottom
        val y = startView.right

        val startRadius = 0
        val endRadius = Math.hypot(baseView.width.toDouble(), baseView.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(baseView, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.start()
    }
}
