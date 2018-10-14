package com.example.z003b2z.twodew.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.z003b2z.twodew.main.model.GenericItem

class ItemDiffUtil(private val old: List<GenericItem>, private val new: List<GenericItem>) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].text == new[newItemPosition].text
    }

}
