package com.example.z003b2z.twodew.android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView

class AnimatedTextSwitcher(context: Context, attributeSet: AttributeSet): TextSwitcher(context, attributeSet) {
    init {
        setFactory { getView() }

        val `in` = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_out_right)

        inAnimation = `in`
        outAnimation = out
    }

    private fun getView(): View {
        val t = TextView(context)
        t.textSize = 22f
        return t
    }
}