package com.example.z003b2z.twodew.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import android.content.res.TypedArray
import android.graphics.Color
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.example.z003b2z.twodew.model.GenericItem
import kotlinx.android.synthetic.main.task_item.view.*


class MainAdapter(private val items: ArrayList<GenericItem>, private val clickListener: OnItemClickListener): RecyclerView.Adapter<MainAdapterViewHolder>() {

    interface OnItemClickListener {
        fun itemClicked(text: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainAdapterViewHolder(parent.inflate(R.layout.task_item, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainAdapterViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            clickListener.itemClicked(items[position].text)
        }
    }

    fun updateData(newData: ArrayList<GenericItem>) {
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

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}