package com.example.z003b2z.twodew.job

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator


class TaskJobCreator : JobCreator {
    @Nullable
    override fun create(@NonNull tag: String): Job? {
        return when (tag) {
            TaskReminderJob.TAG -> TaskReminderJob()
            else -> null
        }
    }
}