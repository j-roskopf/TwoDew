package com.example.z003b2z.twodew.di

import org.koin.dsl.module.module

val WhoItemModule = module {

    // single instance of HelloRepository
    single { WhoItemProvider() }

}