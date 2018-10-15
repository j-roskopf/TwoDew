package com.example.z003b2z.twodew.di

import androidx.room.Room
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.main.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val AppModule = module {
    single { Room.databaseBuilder(androidApplication(), TaskDatabase::class.java, "task-db").fallbackToDestructiveMigration().build() }

    single { get<TaskDatabase>().dao() }

    viewModel { MainViewModel(get()) }
}