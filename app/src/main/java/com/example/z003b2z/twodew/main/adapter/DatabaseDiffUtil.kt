package com.example.z003b2z.twodew.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.model.GenericItem

class DatabaseDiffUtil(private val old: List<Task>, private val new: List<Task>) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id &&
                old[oldItemPosition].`when` == new[newItemPosition].`when`

    }

}
