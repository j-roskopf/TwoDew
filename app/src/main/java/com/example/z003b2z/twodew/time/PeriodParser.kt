package com.example.z003b2z.twodew.time

import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.model.GenericReminderItem
import org.joda.time.LocalDateTime
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

class PeriodParser {

  companion object {
    const val DAY_SUFFIX = "d"
    const val HOUR_SUFFIX = "hr"
    const val MINUTE_SUFFIX = "min"
    const val SECOND_SUFFIX = "sec"

    private val formatter: PeriodFormatter = PeriodFormatterBuilder()
      .appendDays().appendSuffix(PeriodParser.DAY_SUFFIX)
      .appendHours().appendSuffix(PeriodParser.HOUR_SUFFIX)
      .appendMinutes().appendSuffix(PeriodParser.MINUTE_SUFFIX)
      .appendSeconds().appendSuffix(PeriodParser.SECOND_SUFFIX)
      .toFormatter()

    fun getDurationFromWhen(`when`: String): Long {
      val period = formatter.parsePeriod(`when`.replace(" ", ""))
      return period.toStandardDuration().millis
    }

    fun getDateFromWhen(`when`: String, currentTimestamp: Long): LocalDateTime {
      val period = formatter.parsePeriod(`when`.replace(" ", ""))
      return LocalDateTime(currentTimestamp + period.toStandardDuration().millis)
    }

    fun sortDates(newData: ArrayList<Task>): ArrayList<GenericReminderItem> {
      val todayDateTime = LocalDateTime()
      val yesterdayDateTime = todayDateTime.minusDays(1)
      val tomorrowDateTime = todayDateTime.plusDays(1)
      val nextWeekDateTime = todayDateTime.plusWeeks(1)

      val yesterdayKey = "tasks of the past"
      val todayKey = "today"
      val tomorrowKey = "tomorrow"
      val nextWeekKey = "next week"
      val laterKey = "later"

      val map: HashMap<String, ArrayList<GenericReminderItem>> = HashMap()
      val toReturn = arrayListOf<GenericReminderItem>()

      //go over data and sort it out into predefined buckets
      newData.forEach { currentTask ->
        when {
          currentTask.timestamp < yesterdayDateTime.toDateTime().millis -> {
            if (map[yesterdayKey] == null) {
              map[yesterdayKey] = ArrayList()
            }
            map[yesterdayKey]?.add(GenericReminderItem.Body(currentTask))
          }
          currentTask.timestamp < todayDateTime.toDateTime().millis -> {
            if (map[todayKey] == null) {
              map[todayKey] = ArrayList()
            }
            map[todayKey]?.add(GenericReminderItem.Body(currentTask))
          }
          currentTask.timestamp < tomorrowDateTime.toDateTime().millis -> {
            if (map[tomorrowKey] == null) {
              map[tomorrowKey] = ArrayList()
            }
            map[tomorrowKey]?.add(GenericReminderItem.Body(currentTask))
          }
          currentTask.timestamp < nextWeekDateTime.toDateTime().millis -> {
            if (map[nextWeekKey] == null) {
              map[nextWeekKey] = ArrayList()
            }
            map[nextWeekKey]?.add(GenericReminderItem.Body(currentTask))
          }
          else -> {
            if (map[laterKey] == null) {
              map[laterKey] = ArrayList()
            }
            map[laterKey]?.add(GenericReminderItem.Body(currentTask))
          }
        }
      }

      map.forEach { (key, value) ->
        toReturn.add(GenericReminderItem.Header(Task("","",""), key))

        value.forEach { item ->
          toReturn.add(item)
        }
      }

      return toReturn
    }
  }
}