package com.example.z003b2z.twodew.main

import androidx.lifecycle.ViewModel
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.job.TaskReminderJob
import com.example.z003b2z.twodew.redux.Action
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.example.z003b2z.twodew.main.model.TaskItem
import com.example.z003b2z.twodew.time.PeriodParser


class MainViewModel(val db: TaskDatabase) : ViewModel() {

    var currentState: MainScreenState = MainScreenState.Who("")
        set(value) {
            previousState = field
            field = value
        }

    var previousState = currentState

    var currentTask = TaskItem("","","")

    val databaseSubject: BehaviorSubject<Long> = BehaviorSubject.create()
    val bottomSheetDatabaseBehaviorSubject: BehaviorSubject<List<Task>> = BehaviorSubject.create()

    /**
     * Given the current state, return the previous logical state
     */
    fun getPreviousStateFromCurrentState(): MainScreenState {
        val state = when (currentState) {
            is MainScreenState.When -> MainScreenState.What("")
            is MainScreenState.What -> MainScreenState.Who("")
            is MainScreenState.Who -> MainScreenState.Who("")
            is MainScreenState.Confirmation -> MainScreenState.When("")
            else -> previousState
        }

        currentState = state
        return currentState
    }

    fun reduce(action: Action): MainScreenState {
        val newState = when (action) {
            is MainAction.WhoClicked -> MainScreenState.What(action.text)
            is MainAction.WhatClicked -> MainScreenState.When(action.text)
            is MainAction.WhenClicked -> MainScreenState.Confirmation
            is MainAction.DrawerOpened -> MainScreenState.DrawerOpen
            is MainAction.FetchTasks -> MainScreenState.LoadingTasks
            else -> currentState
        }

        currentState = newState
        return currentState
    }

    fun buildNotificationText(): String {
        return currentTask.who + " " + currentTask.what + " " + currentTask.`when`
    }

    fun buildTask(): Task {
        return Task(
                who = currentTask.who,
                what = currentTask.what,
                `when` = currentTask.`when`
        )
    }

    fun insertTask(who: String, what: String, `when`: String) {
        GlobalScope.launch {
            val taskResult = db.taskDao().insertTask(Task(who, what, `when`))
            databaseSubject.onNext(taskResult)
        }
    }

    fun fetchTasks(databaseBehaviorSubject: BehaviorSubject<List<Task>>) {
        GlobalScope.launch {
            val taskResult = db.taskDao().selectAll()
            databaseBehaviorSubject.onNext(taskResult)
        }
    }

    fun scheduleJob(id: Long) {
        val extras = PersistableBundleCompat()
        extras.putLong(JOB_PARAM_ID, id)
        extras.putString(JOB_PARAM_WHO, currentTask.who)
        extras.putString(JOB_PARAM_WHAT, currentTask.what)
        extras.putString(JOB_PARAM_WHEN, currentTask.`when`)
        TaskReminderJob.scheduleJob(extras, PeriodParser.getDurationFromWhem(currentTask.`when`), db, id)
    }

    fun updateCurrentTask(who: CharSequence, what: CharSequence, `when`: CharSequence) {
        currentTask.who = who.toString()
        currentTask.what = what.toString()
        currentTask.`when` = `when`.toString()
    }

    companion object {
        const val INTENT_TEXT = "text"
        const val INTENT_ID = "id"

        const val JOB_PARAM_WHO = "who"
        const val JOB_PARAM_WHAT = "what"
        const val JOB_PARAM_WHEN = "when"
        const val JOB_PARAM_ID = "id"
    }
}