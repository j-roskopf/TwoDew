package com.example.z003b2z.twodew.main.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.extensions.inflate
import com.example.z003b2z.twodew.main.MainAction
import com.example.z003b2z.twodew.main.MainActionHandler
import com.example.z003b2z.twodew.main.MainScreenState
import com.example.z003b2z.twodew.main.model.GenericItem
import kotlinx.android.synthetic.main.task_item.view.*


class MainAdapter(private val items: ArrayList<GenericItem>, private val actionHandler: MainActionHandler, screenState: MainScreenState): RecyclerView.Adapter<MainAdapterViewHolder>() {

    private var screenState: MainScreenState

    init {
        this.screenState = screenState
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainAdapterViewHolder(parent.inflate(R.layout.task_item, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainAdapterViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            when(screenState) {
                is MainScreenState.Who -> actionHandler(MainAction.WhoClicked(items[position].text))
                is MainScreenState.What -> actionHandler(MainAction.WhatClicked(items[position].text))
                is MainScreenState.When -> actionHandler(MainAction.WhenClicked(items[position].text))
            }
        }
    }

    fun updateData(newData: ArrayList<GenericItem>, state: MainScreenState) {
        this.screenState = state
        val result = DiffUtil.calculateDiff(ItemDiffUtil(items, newData), false)
        this.items.clear()
        this.items.addAll(newData)
        result.dispatchUpdatesTo(this)
    }
}

class MainAdapterViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    fun bind(genericItem: GenericItem) {
        view.taskItemBaseLayout.setBackgroundColor(getMatColor("500", view.context))
        view.taskItemText.text = genericItem.text
    }

    private fun getMatColor(typeColor: String, context: Context): Int {
        var returnColor = Color.BLACK
        val arrayId = context.resources.getIdentifier("mdcolor_$typeColor", "array", context.applicationContext.packageName)

        if (arrayId != 0) {
            val colors = context.resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.BLACK)
            colors.recycle()
        }
        return returnColor
    }

}

