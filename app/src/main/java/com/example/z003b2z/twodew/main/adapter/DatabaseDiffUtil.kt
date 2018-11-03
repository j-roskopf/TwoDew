package com.example.z003b2z.twodew.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.model.GenericReminderItem

class DatabaseDiffUtil(private val old: List<GenericReminderItem>, private val new: List<GenericReminderItem>) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].task.id == new[newItemPosition].task.id &&
                old[oldItemPosition].task.`when` == new[newItemPosition].task.`when`

    }

}
