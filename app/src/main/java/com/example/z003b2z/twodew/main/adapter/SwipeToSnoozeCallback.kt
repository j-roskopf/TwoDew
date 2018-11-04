package com.example.z003b2z.twodew.main.adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import timber.log.Timber

abstract class SwipeToSnoozeCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

  private val snoozeIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_snooze_white_24dp)
  private val intrinsicWidth = snoozeIconDrawable?.intrinsicWidth ?: 0
  private val intrinsicHeight = snoozeIconDrawable?.intrinsicHeight ?: 0
  private val background = ColorDrawable()
  private val backgroundColor = Color.parseColor("#EBCB8B")
  private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

  override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
    return when (viewHolder) {
      is BottomSheetBodyViewHolder -> super.getMovementFlags(recyclerView, viewHolder)
      is BottomSheetHeaderViewHolder -> 0
      else -> super.getMovementFlags(recyclerView, viewHolder)
    }
  }

  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    return false
  }

  override fun onChildDraw(
    c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
  ) {
    val itemView = viewHolder.itemView
    val itemHeight = itemView.bottom - itemView.top
    val isCanceled = dX == 0f && !isCurrentlyActive

    if (isCanceled) {
      clearCanvas(
        c,
        itemView.left.toFloat(),
        itemView.top.toFloat(),
        itemView.left.toFloat() + dX,
        itemView.bottom.toFloat()
      )
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      return
    }

    // Draw the red delete background
    background.color = backgroundColor
    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
    background.draw(c)

    // Calculate position of delete icon
    val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
    val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
    val deleteIconLeft = itemView.left + deleteIconMargin
    val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWidth
    val deleteIconBottom = deleteIconTop + intrinsicHeight

    // Draw the delete icon
    snoozeIconDrawable?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
    snoozeIconDrawable?.draw(c)

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }

  private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
    c?.drawRect(left, top, right, bottom, clearPaint)
  }
}