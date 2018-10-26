package com.example.z003b2z.twodew.main.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.extensions.inflate
import com.example.z003b2z.twodew.db.entity.Task
import kotlinx.android.synthetic.main.bottom_sheet_task_item.view.*

class BottomSheetAdapter(private val items: ArrayList<Task>): RecyclerView.Adapter<BottomSheetAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BottomSheetAdapterViewHolder(parent.inflate(R.layout.bottom_sheet_task_item, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BottomSheetAdapterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateData(newData: ArrayList<Task>) {
        val result = DiffUtil.calculateDiff(DatabaseDiffUtil(items, newData), false)
        this.items.clear()
        this.items.addAll(newData)
        result.dispatchUpdatesTo(this)
    }
}

class BottomSheetAdapterViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    fun bind(genericItem: Task) {
        view.bottomSheetTaskItemText.text = genericItem.text
    }

}