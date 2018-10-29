package com.example.z003b2z.twodew.db.dao

import androidx.room.*
import com.example.z003b2z.twodew.db.entity.JobEntity



@Dao
interface JobDao {
    @Insert
    fun insertJob(jobEntity: JobEntity): Long

    @Delete
    fun deleteJob(jobEntity: JobEntity): Int

    @Query("SELECT * FROM job WHERE taskId == :taskId LIMIT 1")
    fun getJobFromTaskId(taskId: Int): Array<JobEntity>
}