package com.example.z003b2z.twodew.db.entity

import androidx.room.PrimaryKey
import androidx.room.Entity


@Entity(tableName = "tasks")
class Task {
    @PrimaryKey lateinit var id: String
    lateinit var text: String
}
