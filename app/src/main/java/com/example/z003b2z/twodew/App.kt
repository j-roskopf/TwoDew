package com.example.z003b2z.twodew

import android.app.Application
import com.example.z003b2z.twodew.di.TaskItemModule
import com.example.z003b2z.twodew.di.WhoItemModule
import org.koin.android.ext.android.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(TaskItemModule, WhoItemModule))
    }
}