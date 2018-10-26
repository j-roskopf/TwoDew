package com.example.z003b2z.twodew.db.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "tasks")
data class Task(@ColumnInfo(name="text") var text: String,
                        @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id: Long = 0)