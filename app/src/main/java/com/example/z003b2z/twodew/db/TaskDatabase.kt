package com.example.z003b2z.twodew.db

import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.z003b2z.twodew.db.dao.JobDao
import com.example.z003b2z.twodew.db.dao.TaskDao
import com.example.z003b2z.twodew.db.entity.JobEntity
import com.example.z003b2z.twodew.db.entity.Task


@Database(entities = [Task::class, JobEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun jobDao(): JobDao
}