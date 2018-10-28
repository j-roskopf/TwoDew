package com.example.z003b2z.twodew.db.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "tasks")
data class Task(@ColumnInfo(name="who") var who: String,
                @ColumnInfo(name="what") var what: String,
                @ColumnInfo(name="when") var `when`: String,
                @ColumnInfo(name="time") var timestamp: Long = System.currentTimeMillis(),
                        @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id: Long = 0)