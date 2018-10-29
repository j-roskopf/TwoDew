package com.example.z003b2z.twodew.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job")
data class JobEntity(@ColumnInfo(name="id") @PrimaryKey var id: Int,
                     @ColumnInfo(name="taskId") var taskId: Int)