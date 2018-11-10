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

class WhenDialogAdapter(private val items: ArrayList<GenericItem>, val listener: (GenericItem) -> Unit) :
  RecyclerView.Adapter<WhenDialogAdapterViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    WhenDialogAdapterViewHolder(parent.inflate(R.layout.task_item, false))

  override fun getItemCount() = items.size

  override fun onBindViewHolder(holder: WhenDialogAdapterViewHolder, position: Int) {
    holder.bind(items[position])
    holder.itemView.setOnClickListener {
      listener(items[position])
    }
  }
}

class WhenDialogAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  fun bind(genericItem: GenericItem) {
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

