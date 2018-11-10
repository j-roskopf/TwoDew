package com.example.z003b2z.twodew.main

import android.app.NotificationManager
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.job.TaskReminderJob
import com.example.z003b2z.twodew.redux.Action
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.GlobalScope
import com.evernote.android.job.JobManager
import kotlinx.coroutines.experimental.launch
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.example.z003b2z.twodew.main.adapter.BottomSheetAdapter
import com.example.z003b2z.twodew.main.model.GenericItem
import com.example.z003b2z.twodew.main.model.GenericReminderItem
import com.example.z003b2z.twodew.main.model.TaskItem
import com.example.z003b2z.twodew.main.ui.MainBottomSheetFragment
import com.example.z003b2z.twodew.time.PeriodParser
import kotlinx.coroutines.experimental.Dispatchers

class MainViewModel(val db: TaskDatabase, private val notificationManager: NotificationManager) : ViewModel() {

  var currentState: MainScreenState = MainScreenState.Who("")
    set(value) {
      previousState = field
      field = value
    }

  var previousState = currentState

  var currentTask = TaskItem("", "", "")

  val databaseSubject: BehaviorSubject<Long> = BehaviorSubject.create()
  val bottomSheetDatabaseBehaviorSubject: BehaviorSubject<ArrayList<GenericReminderItem>> = BehaviorSubject.create()

  /**
   * Given the current state, return the previous logical state
   */
  fun getPreviousStateFromCurrentState(): MainScreenState {
    val state = when (currentState) {
      is MainScreenState.When -> MainScreenState.What("")
      is MainScreenState.What -> MainScreenState.Who("")
      is MainScreenState.Who -> MainScreenState.Who("")
      is MainScreenState.Confirmation -> MainScreenState.When("")
      else -> {
        when {
          currentTask.who == "" -> MainScreenState.Who("")
          currentTask.what == "" -> MainScreenState.Who("")
          currentTask.`when` == "" -> MainScreenState.What("")
          else -> this.currentState
        }
      }
    }

    currentState = state
    return currentState
  }

  fun reduce(action: Action): MainScreenState {
    val newState = when (action) {
      is MainAction.WhoClicked -> MainScreenState.What(action.text)
      is MainAction.WhatClicked -> MainScreenState.When(action.text)
      is MainAction.WhenClicked -> MainScreenState.Confirmation
      is MainAction.FetchTasks -> MainScreenState.LoadingTasks
      else -> currentState
    }

    currentState = newState
    return currentState
  }

  fun buildNotificationText(): String {
    return currentTask.who + " " + currentTask.what
  }

  fun insertTask(who: String, what: String, `when`: String) {
    GlobalScope.launch {
      val taskResult = db.taskDao().insertTask(Task(who, what, `when`))
      databaseSubject.onNext(taskResult)
    }
  }

  fun fetchTasks(databaseBehaviorSubject: BehaviorSubject<ArrayList<GenericReminderItem>>) {
    GlobalScope.launch {
      val taskResult = db.taskDao().selectAll()
      GlobalScope.launch(Dispatchers.Main) {
        databaseBehaviorSubject.onNext(PeriodParser.sortDates(ArrayList(taskResult)))
      }
    }
  }

  fun scheduleJob(id: Long) {
    TaskReminderJob.scheduleJob(
      buildParams(currentTask, id),
      PeriodParser.getDurationFromWhen(currentTask.`when`),
      db,
      id
    )
  }

  fun updateCurrentTask(who: CharSequence, what: CharSequence, `when`: CharSequence) {
    currentTask.who = who.toString()
    currentTask.what = what.toString()
    currentTask.`when` = `when`.toString()
  }

  fun snoozeItem(
    viewHolder: RecyclerView.ViewHolder,
    adapter: BottomSheetAdapter,
    selectedItem: GenericItem
  ) {
    val currentTask = (adapter.items[viewHolder.adapterPosition] as GenericReminderItem.Body).task

    GlobalScope.launch {
      //don't want to call .copy here on currentTask because we want a new ID
      val taskCopy = currentTask.copy(`when` = selectedItem.text, timestamp = System.currentTimeMillis())

      //delete items from the task DB
      deleteItem(currentTask.id.toInt())

      //find current job ID and cancel it
      findAndCancelJob(currentTask.id.toInt())

      //update item
      db.taskDao().insertTask(taskCopy)

      //update data
      val allData = ArrayList(db.taskDao().selectAll())

      TaskReminderJob.scheduleJob(
        MainViewModel.buildParams(currentTask.toTaskItem(), currentTask.id),
        PeriodParser.getDurationFromWhen(currentTask.`when`),
        db,
        taskCopy.id
      )

      GlobalScope.launch(Dispatchers.Main) {
        updateBottomSheet(allData, adapter)
        adapter.notifyItemChanged(viewHolder.adapterPosition)
      }
    }
  }

  private fun updateBottomSheet(newData: ArrayList<Task>, adapter: BottomSheetAdapter) {
    adapter.updateData(PeriodParser.sortDates(newData))
  }

  fun deleteItem(viewHolder: RecyclerView.ViewHolder, fragment: MainBottomSheetFragment) {
    GlobalScope.launch {
      val id = (fragment.adapter.items[viewHolder.adapterPosition] as GenericReminderItem.Body).task.id.toInt()

      //delete items from the task DB
      deleteItem(id)

      //find current job ID and cancel it
      findAndCancelJob(id)

      //remove notification
      notificationManager.cancel(id)

      //update data
      val allData = ArrayList(db.taskDao().selectAll())

      GlobalScope.launch(Dispatchers.Main) {
        fragment.updateData(PeriodParser.sortDates(allData))
      }
    }
  }

  private fun findAndCancelJob(taskId: Int) {
    val job = db.jobDao().getJobFromTaskId(taskId)
    if (job.isNotEmpty()) {
      JobManager.instance().cancel(job.first().id)
    }
  }

  private fun deleteItem(id: Int) {
    db.taskDao().deleteById(id)
  }

  fun validTask(adapter: BottomSheetAdapter, viewHolder: RecyclerView.ViewHolder): Boolean{
    return !(adapter.items[viewHolder.adapterPosition] as GenericReminderItem.Body).task.`when`.equals("never", ignoreCase = true)
  }

  companion object {
    const val INTENT_TEXT = "text"
    const val INTENT_ID = "id"

    const val JOB_PARAM_WHO = "who"
    const val JOB_PARAM_WHAT = "what"
    const val JOB_PARAM_WHEN = "when"
    const val JOB_PARAM_ID = "id"

    fun buildParams(taskItem: TaskItem, id: Long): PersistableBundleCompat {
      val extras = PersistableBundleCompat()
      extras.putLong(JOB_PARAM_ID, id)
      extras.putString(JOB_PARAM_WHO, taskItem.who)
      extras.putString(JOB_PARAM_WHAT, taskItem.what)
      extras.putString(JOB_PARAM_WHEN, taskItem.`when`)
      return extras
    }
  }
}