package com.example.z003b2z.twodew.settings

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.extensions.inflate
import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity
import com.example.z003b2z.twodew.main.MainScreenState
import com.example.z003b2z.twodew.main.adapter.ItemDiffUtil
import com.example.z003b2z.twodew.main.model.GenericItem
import kotlinx.android.synthetic.main.task_item.view.taskItemBaseLayout
import kotlinx.android.synthetic.main.task_item.view.taskItemText

class GenericSettingsItemAdapter(private val items: ArrayList<GenericSettingsEntity>, val listener: (GenericSettingsEntity) -> Unit) :
  RecyclerView.Adapter<GenericSettingsItemAdapterViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    GenericSettingsItemAdapterViewHolder(parent.inflate(R.layout.task_item, false))

  override fun getItemCount() = items.size

  override fun onBindViewHolder(holder: GenericSettingsItemAdapterViewHolder, position: Int) {
    holder.bind(items[holder.adapterPosition])
    holder.itemView.setOnLongClickListener {
      listener(items[holder.adapterPosition])
      true
    }
  }

  fun updateData(newData: List<GenericSettingsEntity>) {
    val result = DiffUtil.calculateDiff(ItemDiffUtil(items, newData), false)
    this.items.clear()
    this.items.addAll(newData)
    result.dispatchUpdatesTo(this)
  }
}

class GenericSettingsItemAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  fun bind(genericItem: GenericSettingsEntity) {
    view.taskItemBaseLayout.setBackgroundColor(getMatColor(view.context))
    view.taskItemText.text = genericItem.text
  }

  private fun getMatColor(context: Context): Int {
    var returnColor = Color.BLACK
    val arrayId = context.resources.getIdentifier("nord", "array", context.applicationContext.packageName)

    if (arrayId != 0) {
      val colors = context.resources.obtainTypedArray(arrayId)
      val index = (Math.random() * colors.length()).toInt()
      returnColor = colors.getColor(index, Color.BLACK)
      colors.recycle()
    }
    return returnColor
  }
}

