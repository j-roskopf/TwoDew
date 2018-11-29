package com.example.z003b2z.twodew.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.db.TaskRepository
import com.example.z003b2z.twodew.main.MainViewModel
import com.example.z003b2z.twodew.notification.NotificationBuilder
import com.example.z003b2z.twodew.time.PeriodParser
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val AppModule = module {
  single {
    TaskDatabase.getInstance(androidContext())
  }
  single { get<TaskDatabase>().taskDao() }

  single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

  single { NotificationBuilder() }

  single { TaskRepository(get()) }

  single { PeriodParser() }

  single { androidContext().getSharedPreferences(androidContext().getString(R.string.app_name), Context.MODE_PRIVATE) }

  viewModel { MainViewModel(get(), get(), get()) }
}