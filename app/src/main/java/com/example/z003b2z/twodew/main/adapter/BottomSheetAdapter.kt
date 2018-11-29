package com.example.z003b2z.twodew.main.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.extensions.inflate
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.model.GenericReminderItem
import com.example.z003b2z.twodew.time.PeriodParser
import kotlinx.android.synthetic.main.bottom_sheet_task_item.view.*
import kotlinx.android.synthetic.main.bottom_sheet_task_item_header.view.headerText
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.koin.standalone.KoinComponent

const val TYPE_HEADER = 0
const val TYPE_BODY = 1

class BottomSheetAdapter(val items: ArrayList<GenericReminderItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
  KoinComponent {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      TYPE_HEADER -> BottomSheetHeaderViewHolder(parent.inflate(R.layout.bottom_sheet_task_item_header, false))
      TYPE_BODY -> BottomSheetBodyViewHolder(parent.inflate(R.layout.bottom_sheet_task_item, false))
      else -> throw IllegalArgumentException("Boo you suck")
    }
  }

  override fun getItemCount() = items.size

  override fun getItemViewType(position: Int): Int {
    return when (items[position]) {
      is GenericReminderItem.Body -> TYPE_BODY
      is GenericReminderItem.Header -> TYPE_HEADER
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is BottomSheetBodyViewHolder -> holder.bind(items[position].task)
      is BottomSheetHeaderViewHolder -> holder.bind((items[position] as GenericReminderItem.Header).displayString)
    }
  }

  fun updateData(newData: ArrayList<GenericReminderItem>) {
    val result = DiffUtil.calculateDiff(DatabaseDiffUtil(items, newData), true)
    this.items.clear()
    this.items.addAll(newData)
    result.dispatchUpdatesTo(this)
  }
}

class BottomSheetBodyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  private var dtf = DateTimeFormat.forPattern("MMM dd YYYY, hh:mm a")

  @SuppressLint("SetTextI18n")
  fun bind(genericItem: Task) {
    view.bottomSheetTaskItemText.text = genericItem.who + " " + genericItem.what
    if (genericItem.`when`.toLowerCase().contains("never")) {
      view.bottomSheetTaskItemTime.text = "Never"
    } else {
      view.bottomSheetTaskItemTime.text = dtf.print(getDuration(genericItem.`when`, genericItem.timestamp))
    }
  }

  private fun getDuration(`when`: String, timestamp: Long): LocalDateTime {
    return PeriodParser.getDateFromWhen(`when`, timestamp)
  }
}

class BottomSheetHeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  fun bind(displayText: String) {
    view.headerText.text = displayText
    if(displayText.equals("never", ignoreCase = true)) {
      view.headerText.setOnLongClickListener {
        AlertDialog.Builder(view.context)
          .setTitle("Confirmation")
          .setMessage("Delete all items in this group?")
          .setPositiveButton("Yup") { _, _ ->
            //TODO Joe implement me
          }
          .setNegativeButton("Nope") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
        true
      }
    }
  }
}