package com.example.z003b2z.twodew.di.tasks

import com.example.z003b2z.twodew.main.model.GenericItem

class TaskItemProvider {
  fun provideListOfTaskItems() = arrayListOf(
    GenericItem("help with"),
    GenericItem("meet with"),
    GenericItem("pay"),
    GenericItem("get"),
    GenericItem("give"),
    GenericItem("discuss"),
    GenericItem("arrange"),
    GenericItem("write"),
    GenericItem("call"),
    GenericItem("buy"),
    GenericItem("write")
  )
}