package com.example.z003b2z.twodew.di

import com.example.z003b2z.twodew.model.WhoItem
import org.koin.dsl.module.module

val WhoItemModule = module {

    // single instance of HelloRepository
    single { WhoItemProvider() }

}