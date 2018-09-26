package com.example.z003b2z.twodew.model

sealed class ScreenState {
    object WHO: ScreenState()
    object WHAT: ScreenState()
    object WHEN: ScreenState()
}