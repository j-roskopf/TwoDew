package com.example.z003b2z.twodew.db.dao

import androidx.room.*
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.model.GenericItem
import io.reactivex.Single

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task): Long

    @Update
    fun updateTask(task: Task): Int

    @Delete
    fun deleteTask(task: Task): Int

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("SELECT * FROM tasks")
    fun selectAll(): List<Task>
}