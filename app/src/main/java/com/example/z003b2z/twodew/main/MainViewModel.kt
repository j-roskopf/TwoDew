package com.example.z003b2z.twodew.main

import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.redux.Action
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MainViewModel(val db: TaskDatabase) : ViewModel() {

    var currentState: MainScreenState = MainScreenState.Who("")
        set(value) {
            previousState = field
            field = value
        }

    var previousState = currentState

    var currentTask = Task("")

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

    fun buildNotificationText(vararg textViews: TextView): String {
        var toReturn = ""
        for (textView in textViews) {
            toReturn += textView.text.toString() + " "
        }
        return toReturn
    }

    fun insertTask(text: String) {
        GlobalScope.launch {
            val taskResult = db.dao().insertTask(Task(text))
            databaseSubject.onNext(taskResult)
        }
    }

    fun fetchTasks(databaseBehaviorSubject: BehaviorSubject<List<Task>>) {
        GlobalScope.launch {
            delay(1000)
            val taskResult = db.dao().selectAll()
            databaseBehaviorSubject.onNext(taskResult)
        }
    }

    companion object {
        const val INTENT_TEXT = "text"
        const val INTENT_ID = "id"
    }
}