package com.example.z003b2z.twodew.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.main.ui.MainActivity
import com.example.z003b2z.twodew.main.MainViewModel
import androidx.core.content.ContextCompat.getSystemService
import android.app.NotificationChannel
import android.os.Build

class NotificationBuilder {

  companion object {
    // if this is changed, update it in the manifest
    const val CANCEL_ACTION = "notification_cancelled"
  }

  fun build(context: Context, id: Int, text: String, notificationManager: NotificationManager): NotificationCompat.Builder {
    val color = ContextCompat.getColor(context, R.color.colorPrimary)

    val CHANNEL_ID = "my_channel_01"// The id of the channel.

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notifyID = 1
      val name = "Reminders"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
      notificationManager.createNotificationChannel(mChannel)
    }


    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.notification_icon)
      .setColor(color)
      .setContentTitle(context.resources.getString(R.string.app_name))
      .setContentText(text)
      .setChannelId(CHANNEL_ID)

    // Creates an explicit intent for an Activity in your app
    val resultIntent = Intent(context, MainActivity::class.java)
    resultIntent.putExtra(MainViewModel.INTENT_TEXT, text)

    if (false) {
      //make persistent
      builder.setOngoing(true)
    }

    resultIntent.putExtra(MainViewModel.INTENT_ID, id)

    // The stack builder object will contain an artificial back stack for the
    // started Activity.
    // This ensures that navigating backward from the Activity leads out of
    // your application to the Home screen.
    val stackBuilder = TaskStackBuilder.create(context)
    // Adds the back stack for the Intent (but not the Intent itself)
    stackBuilder.addParentStack(MainActivity::class.java)
    // Adds the Intent that starts the Activity to the top of the stack
    stackBuilder.addNextIntent(resultIntent)

    val resultPendingIntent = PendingIntent.getActivity(context, id, resultIntent, 0)

    builder.setDeleteIntent(getDeleteIntent(context, id))

    builder.setContentIntent(resultPendingIntent)

    return builder
  }

  private fun getDeleteIntent(context: Context, Id: Int): PendingIntent {
    val intent = Intent(context, NotificationDeleted::class.java)
    intent.action = CANCEL_ACTION
    intent.putExtra(MainViewModel.INTENT_ID, Id)
    return PendingIntent.getBroadcast(context, Id + 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
  }
}