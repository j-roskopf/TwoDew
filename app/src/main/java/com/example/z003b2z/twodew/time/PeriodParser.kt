package com.example.z003b2z.twodew.time

import org.joda.time.LocalDateTime
import org.joda.time.format.PeriodFormatterBuilder

class PeriodParser {

    companion object {
        const val DAY_SUFFIX = "d"
        const val HOUR_SUFFIX = "hr"
        const val MINUTE_SUFFIX = "min"
        const val SECOND_SUFFIX = "sec"

        val formatter = PeriodFormatterBuilder()
                .appendDays().appendSuffix(PeriodParser.DAY_SUFFIX)
                .appendHours().appendSuffix(PeriodParser.HOUR_SUFFIX)
                .appendMinutes().appendSuffix(PeriodParser.MINUTE_SUFFIX)
                .appendSeconds().appendSuffix(PeriodParser.SECOND_SUFFIX)
                .toFormatter()

        fun getDurationFromWhem(`when`: String): Long {
            val period = formatter.parsePeriod(`when`.replace(" ",""))
            return period.toStandardDuration().millis
        }

        fun getDateFromWhem(`when`: String, currentTimestamp: Long): LocalDateTime {
            val period = formatter.parsePeriod(`when`.replace(" ",""))
            return LocalDateTime(currentTimestamp + period.toStandardDuration().millis)
        }

    }


}