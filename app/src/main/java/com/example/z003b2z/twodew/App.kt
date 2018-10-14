package com.example.z003b2z.twodew

import android.app.Application
import com.example.z003b2z.twodew.di.*
import org.koin.android.ext.android.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(TaskItemModule, WhoItemModule, WhenItemModule, AnimationModule, AppModule))
    }
}