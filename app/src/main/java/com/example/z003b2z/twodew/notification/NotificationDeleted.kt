package com.example.z003b2z.twodew.notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.main.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


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
            db.taskDao().deleteById(id)
        }
    }
}