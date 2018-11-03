package com.example.z003b2z.twodew.di.tasks

import com.example.z003b2z.twodew.main.model.GenericItem
import com.example.z003b2z.twodew.time.PeriodParser

class WhenItemProvider {
    fun provideListOfWhenItems() = arrayListOf(
            GenericItem("Never"),
            GenericItem("3 ".plus(PeriodParser.SECOND_SUFFIX)),
            GenericItem("10 ".plus(PeriodParser.SECOND_SUFFIX)),
            GenericItem("5 ".plus(PeriodParser.MINUTE_SUFFIX)),
            GenericItem("15 ".plus(PeriodParser.MINUTE_SUFFIX)),
            GenericItem("30 ".plus(PeriodParser.MINUTE_SUFFIX)),
            GenericItem("45 ".plus(PeriodParser.MINUTE_SUFFIX)),
            GenericItem("1 ".plus(PeriodParser.HOUR_SUFFIX)),
            GenericItem("2 ".plus(PeriodParser.HOUR_SUFFIX)),
            GenericItem("3 ".plus(PeriodParser.HOUR_SUFFIX)),
            GenericItem("4 ".plus(PeriodParser.HOUR_SUFFIX)),
            GenericItem("5 ".plus(PeriodParser.HOUR_SUFFIX))
    )
}