package com.example.z003b2z.twodew.main

const val STATE_NONE = ""

sealed class MainScreenState(val text: String) {
    class Who(text: String) : MainScreenState(text)
    class What(text: String) : MainScreenState(text)
    class When(text: String) : MainScreenState(text)
    object Confirmation : MainScreenState(STATE_NONE)
    object LoadingTasks : MainScreenState(STATE_NONE)
}