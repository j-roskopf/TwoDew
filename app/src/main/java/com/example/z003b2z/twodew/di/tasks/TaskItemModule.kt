package com.example.z003b2z.twodew.di.tasks

import org.koin.dsl.module.module

val TaskItemModule = module {

    // single instance of HelloRepository
    single { TaskItemProvider() }

}