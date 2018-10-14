package com.example.z003b2z.twodew.di

import com.example.z003b2z.twodew.main.MainViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val AppModule = module {

    // MyViewModel ViewModel
    viewModel { MainViewModel() }
}