package com.example.z003b2z.twodew.main.model

import android.net.Uri
import com.example.z003b2z.twodew.db.entity.Task

sealed class GenericReminderItem(val task: Task) {
  class Body(task: Task) : GenericReminderItem(task)
  class Header(task: Task) : GenericReminderItem(task)
}