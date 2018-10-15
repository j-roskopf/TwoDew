package com.example.z003b2z.twodew.di.tasks

import org.koin.dsl.module.module

val WhoItemModule = module {

    // single instance of HelloRepository
    single { WhoItemProvider() }

}