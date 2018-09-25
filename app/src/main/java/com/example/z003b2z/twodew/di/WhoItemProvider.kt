package com.example.z003b2z.twodew.di

import com.example.z003b2z.twodew.model.GenericItem
import com.example.z003b2z.twodew.model.TaskItem
import com.example.z003b2z.twodew.model.WhoItem

class WhoItemProvider {
    fun provideListOfWhoItems() = arrayListOf(
            GenericItem("me"),
            GenericItem("friend"),
            GenericItem("S/O"),
            GenericItem("house"),
            GenericItem("work"),
            GenericItem("doctor")
    )
}