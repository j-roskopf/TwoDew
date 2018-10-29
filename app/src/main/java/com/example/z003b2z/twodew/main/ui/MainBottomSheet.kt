package com.example.z003b2z.twodew.main.ui

import android.app.NotificationManager
import android.app.job.JobScheduler
import android.content.Context
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evernote.android.job.JobManager
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.job.TaskJobCreator
import com.example.z003b2z.twodew.job.TaskReminderJob
import com.example.z003b2z.twodew.main.adapter.BottomSheetAdapter
import com.example.z003b2z.twodew.main.adapter.SwipeToDismissCallback
import kotlinx.android.synthetic.main.main_bottom_sheet.view.*
import kotlinx.coroutines.experimental.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import com.example.z003b2z.twodew.main.adapter.SwipeToSnoozeCallback
import com.example.z003b2z.twodew.time.PeriodParser


class MainBottomSheet(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet), KoinComponent {

    private var adapter: BottomSheetAdapter = BottomSheetAdapter(arrayListOf())
    private val taskDatabase: TaskDatabase by inject()
    private val notificationManager  by lazy {  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

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
                snoozeItem(viewHolder)
            }
        }
        val swipeToSnoozeItemTouchHelper = ItemTouchHelper(swipeToSnoozeHandler)
        swipeToSnoozeItemTouchHelper.attachToRecyclerView(bottomSheetContent)

        bottomSheetContent.adapter = adapter
        bottomSheetContent.layoutManager = LinearLayoutManager(context)
    }

    private fun snoozeItem(viewHolder: RecyclerView.ViewHolder) {
        GlobalScope.launch {
            val currentTaskId = adapter.items[viewHolder.adapterPosition].id.toInt()
            val currentTask = adapter.items[viewHolder.adapterPosition]

            //don't want to call .copy here on currentTask because we want a new ID
            val taskCopy = currentTask.copy(`when` = "10 min", timestamp = System.currentTimeMillis())

            //delete items from the task DB
            deleteItem(currentTaskId)

            //find current job ID and cancel it
            findAndCancelJob(currentTaskId)

            //update item
            taskDatabase.taskDao().insertTask(taskCopy)

            //update data
            val allData = ArrayList(taskDatabase.taskDao().selectAll())

            val extras = PersistableBundleCompat()

            TaskReminderJob.scheduleJob(extras, PeriodParser.getDurationFromWhem(currentTask.`when`), taskDatabase, taskCopy.id)

            GlobalScope.launch(Dispatchers.Main) {
                updateData(allData)
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        }
    }

    private fun deleteItem(viewHolder: RecyclerView.ViewHolder) {
        GlobalScope.launch {
            val id = adapter.items[viewHolder.adapterPosition].id.toInt()

            //delete items from the task DB
            deleteItem(id)

            //find current job ID and cancel it
            findAndCancelJob(id)

            //remove notification
            notificationManager.cancel(id)

            //update data
            val allData = ArrayList(taskDatabase.taskDao().selectAll())

            GlobalScope.launch(Dispatchers.Main) {
                updateData(allData)
            }
        }
    }

    private fun findAndCancelJob(taskId: Int) {
        val job = taskDatabase.jobDao().getJobFromTaskId(taskId)
        if(job.isNotEmpty()) {
            JobManager.instance().cancel(job.first().id)
        }
    }

    private fun deleteItem(id: Int) {
        taskDatabase.taskDao().deleteById(id)
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