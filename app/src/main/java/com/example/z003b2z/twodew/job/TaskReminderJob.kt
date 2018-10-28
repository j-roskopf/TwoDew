package com.example.z003b2z.twodew.job

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.example.z003b2z.twodew.notification.NotificationBuilder
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber


class TaskReminderJob : Job(), KoinComponent {

    private val notificationBuilder: NotificationBuilder by inject()

    @NonNull
    override fun onRunJob(params: Params): Result {
        val id = ((Math.random() * 100) + 100).toInt()
        // run your job here
        val builder = notificationBuilder.build(context,
                ((Math.random() * 100) + 100).toInt(),
                "reminding you about your notification"
        )
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(id, builder.build())

        return Result.SUCCESS
    }

    companion object {

        val TAG = "task_reminder"

        fun scheduleJob() {
            JobRequest.Builder(TAG)
                    .setExact(5000L)
                    .build()
                    .schedule()
        }
    }
}