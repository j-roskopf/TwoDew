package com.example.z003b2z.twodew.di

import com.example.z003b2z.twodew.model.GenericItem
import com.example.z003b2z.twodew.model.TaskItem

class TaskItemProvider {
    fun provideListOfTaskItems() = arrayListOf(
            GenericItem("Help with"),
            GenericItem("Meet with"),
            GenericItem("Pay"),
            GenericItem("Get"),
            GenericItem("Give"),
            GenericItem("Discuss"),
            GenericItem("Arrange"),
            GenericItem("Write"),
            GenericItem("Call")
    )
}