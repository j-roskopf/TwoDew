package com.example.z003b2z.twodew.android

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import com.example.z003b2z.twodew.R
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.android.extensions.setDuration
import com.example.z003b2z.twodew.di.tasks.WhenItemProvider
import com.example.z003b2z.twodew.main.adapter.WhenDialogAdapter
import com.example.z003b2z.twodew.main.model.GenericItem
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.custom_input_layout_dialog.view.customPromptRecyclerView
import kotlinx.android.synthetic.main.custom_input_layout_dialog.view.customPromptTextInput
import kotlinx.android.synthetic.main.when_layout_dialog.view.whenDialogRecyclerView
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@SuppressLint("InflateParams")
class PromptDialog(context: Context, val listener: (input: String?, `when`: String?) -> Unit) : AlertDialog(context),
  KoinComponent {

  private val whenItemProvider: WhenItemProvider by inject()
  lateinit var adapter: WhenDialogAdapter

  init {
    val view = LayoutInflater.from(context).inflate(R.layout.custom_input_layout_dialog, null)
    setView(view)

    setupRecyclerView(view)
    setCancelButton()
  }

  private fun setCancelButton() {
    setCancelable(true)
    setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
      listener(null, null)
      dismiss()
    }
    setOnCancelListener {
      listener(null, null)
      dismiss()
    }
  }

  private fun setupRecyclerView(view: View) {
    view.customPromptRecyclerView.layoutManager = GridLayoutManager(context, 4)
    adapter = WhenDialogAdapter(whenItemProvider.provideListOfWhenItems()) {
      listener(view.customPromptTextInput.text.toString(), it.text)
      dismiss()
    }
    view.customPromptRecyclerView.adapter = adapter

    val animator = LandingAnimator(OvershootInterpolator(1f)).setDuration()
    view.customPromptRecyclerView.itemAnimator = animator
  }
}