package com.example.z003b2z.twodew

import android.app.Application
import androidx.room.Room
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.di.*
import com.example.z003b2z.twodew.di.tasks.TaskItemModule
import com.example.z003b2z.twodew.di.tasks.WhenItemModule
import com.example.z003b2z.twodew.di.tasks.WhoItemModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber.DebugTree
import timber.log.Timber



class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(TaskItemModule, WhoItemModule, WhenItemModule, AnimationModule, AppModule))
        if (BuildConfig.DEBUG){
            Timber.plant(DebugTree())
        }
    }
}