package com.example.z003b2z.twodew.main.ui

import android.content.Context
import android.provider.Contacts
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.android.extensions.plusAssign
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.adapter.BottomSheetAdapter
import com.example.z003b2z.twodew.main.adapter.SwipeToDismissCallback
import com.example.z003b2z.twodew.main.model.GenericItem
import com.google.android.gms.tasks.Tasks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.main_bottom_sheet.view.*
import kotlinx.coroutines.experimental.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import android.widget.Toast
import com.example.z003b2z.twodew.main.adapter.SwipeToSnoozeCallback


class MainBottomSheet(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet), KoinComponent {

    private var adapter: BottomSheetAdapter = BottomSheetAdapter(arrayListOf())
    private val taskDatabase: TaskDatabase by inject()

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val swipeToDismissHandler = object : SwipeToDismissCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteItem(viewHolder)
            }
        }
        val swipeToDismissItemTouchHelper = ItemTouchHelper(swipeToDismissHandler)
        swipeToDismissItemTouchHelper.attachToRecyclerView(bottomSheetContent)

        val swipeToSnoozeHandler = object : SwipeToSnoozeCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }
        val swipeToSnoozeItemTouchHelper = ItemTouchHelper(swipeToSnoozeHandler)
        swipeToSnoozeItemTouchHelper.attachToRecyclerView(bottomSheetContent)

        bottomSheetContent.adapter = adapter
        bottomSheetContent.layoutManager = LinearLayoutManager(context)
    }

    private fun deleteItem(viewHolder: RecyclerView.ViewHolder) {
        GlobalScope.launch {
            deleteItem(adapter.items[viewHolder.adapterPosition].id.toInt())

            val allData = ArrayList(taskDatabase.dao().selectAll())

            GlobalScope.launch(Dispatchers.Main) {
                updateData(allData)
            }
        }
    }

    private fun deleteItem(id: Int) {
        taskDatabase.dao().deleteById(id)
    }

    fun updateData(newData: ArrayList<Task>) {
        adapter.updateData(newData)
    }

    fun setStateVisibility(
            loading: Int = View.GONE,
            base: Int = View.GONE
    ) {
        bottomSheetProgress.visibility = loading
        bottomSheetContent.visibility = base
    }
}