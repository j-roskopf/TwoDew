package com.example.z003b2z.twodew.job

import android.app.NotificationManager
import android.content.Context
import androidx.annotation.NonNull
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.entity.JobEntity
import com.example.z003b2z.twodew.main.MainViewModel.Companion.JOB_PARAM_WHAT
import com.example.z003b2z.twodew.main.MainViewModel.Companion.JOB_PARAM_WHO
import com.example.z003b2z.twodew.notification.NotificationBuilder
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


class TaskReminderJob : Job(), KoinComponent {

    private val notificationBuilder: NotificationBuilder by inject()

    @NonNull
    override fun onRunJob(params: Params): Result {
        val id = ((Math.random() * 100) + 100).toInt()

        val builder = notificationBuilder.build(context,
                -1,
                "reminding ${params.extras[JOB_PARAM_WHO]} about ${params.extras[JOB_PARAM_WHAT]}"
        )
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(id, builder.build())

        return Result.SUCCESS
    }

    companion object {

        val TAG = "task_reminder"

        fun scheduleJob(extras: PersistableBundleCompat, durationFromWhen: Long, database: TaskDatabase, taskId: Long) {
            val id = JobRequest.Builder(TAG)
                    .setExact(durationFromWhen)
                    .setExtras(extras)
                    .build()
                    .schedule()

            GlobalScope.launch {
                database.jobDao().insertJob(JobEntity(id, taskId.toInt()))
            }
        }
    }
}