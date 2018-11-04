package com.example.z003b2z.twodew.main.ui

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.z003b2z.twodew.main.adapter.BottomSheetAdapter
import com.example.z003b2z.twodew.main.adapter.SwipeToDismissCallback
import com.example.z003b2z.twodew.main.adapter.SwipeToSnoozeCallback
import com.example.z003b2z.twodew.main.model.GenericReminderItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.z003b2z.twodew.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.main_bottom_sheet.bottomSheetContent
import kotlinx.android.synthetic.main.main_bottom_sheet.bottomSheetProgress

//TODO JOE GIVE SOME ROUNDED CORNERS

class MainBottomSheetFragment : BottomSheetDialogFragment() {

  var adapter: BottomSheetAdapter = BottomSheetAdapter(arrayListOf())
  private lateinit var swipeToSnoozeCallback: ItemTouchHelper
  private lateinit var swipeToDismissCallback: ItemTouchHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.TransparentDialog)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.main_bottom_sheet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupRecyclerView()

    setupBottomSheet()

    dialog.setOnShowListener {
      val d = dialog as BottomSheetDialog
      val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
      BottomSheetBehavior.from(bottomSheetInternal!!).setState(BottomSheetBehavior.STATE_EXPANDED)
    }
  }

  private fun setupRecyclerView() {
    bottomSheetContent.adapter = adapter
    bottomSheetContent.layoutManager = LinearLayoutManager(context)
  }

  fun updateData(newData: ArrayList<GenericReminderItem>) {
    adapter.updateData(newData)
  }

  private fun setupBottomSheet() {
    swipeToDismissCallback.attachToRecyclerView(bottomSheetContent)
    swipeToSnoozeCallback.attachToRecyclerView(bottomSheetContent)
  }

  fun setStateVisibility(
    loading: Int = View.GONE,
    base: Int = View.GONE
  ) {
    bottomSheetProgress.visibility = loading
    bottomSheetContent.visibility = base
  }

  fun setSwipeListeners(swipeToDismissHandler: SwipeToDismissCallback, swipeToSnoozeHandler: SwipeToSnoozeCallback) {
    swipeToDismissCallback = ItemTouchHelper(swipeToDismissHandler)
    swipeToSnoozeCallback = ItemTouchHelper(swipeToSnoozeHandler)
  }
}