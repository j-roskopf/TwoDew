package com.example.z003b2z.twodew.di

import com.example.z003b2z.twodew.main.model.GenericItem

class WhenItemProvider {
    fun provideListOfWhenItems() = arrayListOf(
            GenericItem("15 min"),
            GenericItem("30 min"),
            GenericItem("45 min"),
            GenericItem("1 hr"),
            GenericItem("2 hr"),
            GenericItem("5 hr")
    )
}