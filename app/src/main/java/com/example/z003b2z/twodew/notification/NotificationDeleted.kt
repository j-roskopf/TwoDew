package com.example.z003b2z.twodew.notification
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.main.MainViewModel
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber


class NotificationDeleted : BroadcastReceiver(), KoinComponent {

    private val db: TaskDatabase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(MainViewModel.INTENT_ID, -1)
        if(id >= 0) {
            deleteNotification(id)
        }
    }

    /**
     * deletes notification
     * @param id
     */
    private fun deleteNotification(id: Int) {
        GlobalScope.launch {
            db.dao().deleteById(id)
        }
    }
}