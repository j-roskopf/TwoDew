package com.example.z003b2z.twodew.di.tasks

import com.example.z003b2z.twodew.main.model.GenericItem

class WhoItemProvider {
    fun provideListOfWhoItems() = arrayListOf(
            GenericItem("me"),
            GenericItem("friend"),
            GenericItem("partner"),
            GenericItem("house"),
            GenericItem("work"),
            GenericItem("doctor")
    )
}