package com.example.z003b2z.twodew.di

import com.example.z003b2z.twodew.animation.Reveal
import org.koin.dsl.module.module

val AnimationModule = module {
    single { Reveal() }
}