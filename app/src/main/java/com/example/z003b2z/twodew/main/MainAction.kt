package com.example.z003b2z.twodew.main

import com.example.z003b2z.twodew.redux.Action

typealias MainActionHandler = (MainAction) -> Unit

sealed class MainAction(val displayText: Boolean = true): Action() {
    class WhoClicked(val text: String = ""): MainAction()
    class WhatClicked(val text: String = ""): MainAction()
    class WhenClicked(val text: String = ""): MainAction()
    class BackClicked(val text: String = ""): MainAction()
    object DrawerOpened: MainAction()
    object FetchTasks : Action()
}