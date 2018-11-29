package com.example.z003b2z.twodew.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.z003b2z.twodew.R
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.R.id.mainRecyclerView
import com.example.z003b2z.twodew.android.extensions.setDuration
import com.example.z003b2z.twodew.di.tasks.WhenItemProvider
import com.example.z003b2z.twodew.main.adapter.MainAdapter
import com.example.z003b2z.twodew.main.adapter.WhenDialogAdapter
import com.example.z003b2z.twodew.main.model.GenericItem
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.when_layout_dialog.view.whenDialogRecyclerView
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@SuppressLint("InflateParams")
class WhenDialog(context: Context, val listener: (GenericItem?) -> Unit) : AlertDialog(context), KoinComponent {

  private val whenItemProvider: WhenItemProvider by inject()
  lateinit var adapter: WhenDialogAdapter

  init {
    setTitle("Choose a time")
    val view = LayoutInflater.from(context).inflate(R.layout.when_layout_dialog, null)
    setView(view)

    setupRecyclerView(view)
    setCancelButton()
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

  private fun setupRecyclerView(view: View) {
    view.whenDialogRecyclerView.layoutManager = GridLayoutManager(context, 4)
    adapter = WhenDialogAdapter(whenItemProvider.provideListOfWhenItems()) {
      listener(it)
      dismiss()
    }
    view.whenDialogRecyclerView.adapter = adapter

    val animator = LandingAnimator(OvershootInterpolator(1f)).setDuration()
    view.whenDialogRecyclerView.itemAnimator = animator
  }
}