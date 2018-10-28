package com.example.z003b2z.twodew.main.model

class TaskItem(var who: String, var what: String, var `when`: String): GenericItem(who + what + `when`)