package com.example.z003b2z.twodew.main

import com.example.z003b2z.twodew.redux.State

sealed class MainScreenState(val text: String): State() {
    class Who(text: String): MainScreenState(text)
    class What(text: String): MainScreenState(text)
    class When(text: String): MainScreenState(text)
    object Confirmation : MainScreenState("")
}