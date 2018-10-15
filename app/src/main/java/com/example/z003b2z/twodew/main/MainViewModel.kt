package com.example.z003b2z.twodew.main

import androidx.lifecycle.ViewModel
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.redux.Action

class MainViewModel(val db: TaskDatabase): ViewModel() {

    var currentState: MainScreenState = MainScreenState.Who("")

    /**
     * Given the current state, return the previous logical state
     */
    fun getPreviousStateFromCurrentState(): MainScreenState {
        val previousState = when(currentState) {
            is MainScreenState.When -> MainScreenState.What("")
            is MainScreenState.What -> MainScreenState.Who("")
            is MainScreenState.Who -> MainScreenState.Who("")
            is MainScreenState.Confirmation -> MainScreenState.When("")
        }

        currentState = previousState
        return currentState
    }

    fun reduce(action: Action): MainScreenState {
        val newState = when (currentState) {
            is MainScreenState.Who -> {
                when (action) {
                    is MainAction.WhoClicked -> MainScreenState.What(action.text)
                    else -> MainScreenState.Who("")
                }
            }
            is MainScreenState.What -> {
                when (action) {
                    is MainAction.WhatClicked -> MainScreenState.When(action.text)
                    else -> MainScreenState.What("")
                }
            }
            is MainScreenState.When -> {
                when (action) {
                    is MainAction.WhenClicked -> MainScreenState.Confirmation
                    else -> MainScreenState.What("")
                }
            }
            else -> currentState
        }

        currentState = newState
        return currentState
    }
}