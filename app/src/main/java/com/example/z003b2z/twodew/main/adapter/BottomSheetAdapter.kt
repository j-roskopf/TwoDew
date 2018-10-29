package com.example.z003b2z.twodew.main.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.extensions.inflate
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.time.PeriodParser
import kotlinx.android.synthetic.main.bottom_sheet_task_item.view.*
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.PeriodFormatterBuilder
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class BottomSheetAdapter(val items: ArrayList<Task>) : RecyclerView.Adapter<BottomSheetAdapterViewHolder>(), KoinComponent {

    private val periodParser: PeriodParser by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BottomSheetAdapterViewHolder(parent.inflate(R.layout.bottom_sheet_task_item, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BottomSheetAdapterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateData(newData: ArrayList<Task>) {
        val result = DiffUtil.calculateDiff(DatabaseDiffUtil(items, newData))
        this.items.clear()
        this.items.addAll(newData)
        result.dispatchUpdatesTo(this)
    }
}

class BottomSheetAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private var dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")

    @SuppressLint("SetTextI18n")
    fun bind(genericItem: Task) {
        view.bottomSheetTaskItemText.text = genericItem.who + " " + genericItem.what
        if(genericItem.`when`.toLowerCase().contains("never")) {
            view.bottomSheetTaskItemTime.text = "Never"
        } else {
            view.bottomSheetTaskItemTime.text = dtf.print(getDuration(genericItem.`when`, genericItem.timestamp))
        }
    }

    private fun getDuration(`when`: String, timestamp: Long): LocalDateTime {
        return PeriodParser.getDateFromWhem(`when`, timestamp)
    }
}