package com.example.z003b2z.twodew.time

import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.model.GenericReminderItem
import org.joda.time.LocalDateTime
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder
import java.util.TreeMap

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
      val tomorrowDateTime = todayDateTime.plusDays(1)
      val twoDaysFromNow = todayDateTime.plusDays(2)
      val nextWeekDateTime = todayDateTime.plusWeeks(1)

      val overdueKey = "overdue"
      val todayKey = "today"
      val tomorrowKey = "tomorrow"
      val thisWeekKey = "this week"
      val laterKey = "later"
      val neverKey = "never"

      //order we want to display to the user
      val keyOrder = arrayOf(todayKey, tomorrowKey, thisWeekKey, laterKey, overdueKey, neverKey)

      val map: TreeMap<String, ArrayList<GenericReminderItem>> = TreeMap(Comparator<String> { p0, p1 ->
        if (keyOrder.indexOf(p0) >= 0 && keyOrder.indexOf(p1) >= 0) {
          keyOrder.indexOf(p0).compareTo(keyOrder.indexOf(p1))
        } else {
          p0.compareTo(p1)
        }
      })
      val toReturn = arrayListOf<GenericReminderItem>()

      //go over data and sort it out into predefined buckets
      newData.forEach { currentTask ->

        if (currentTask.`when`.equals("never", ignoreCase = true)) {
          if (map[neverKey] == null) {
            map[neverKey] = ArrayList()
          }
          map[neverKey]?.add(GenericReminderItem.Body(currentTask))
        } else {
          val elapsedTime = getDateFromWhen(
            currentTask.`when`,
            currentTask.timestamp
          ).toDateTime().millis

          when {
            elapsedTime < todayDateTime.toDateTime().millis -> {
              if (map[overdueKey] == null) {
                map[overdueKey] = ArrayList()
              }
              map[overdueKey]?.add(GenericReminderItem.Body(currentTask))
            }
            elapsedTime >= todayDateTime.toDateTime().millis
              && elapsedTime < tomorrowDateTime.toDateTime().millis -> {
              if (map[todayKey] == null) {
                map[todayKey] = ArrayList()
              }
              map[todayKey]?.add(GenericReminderItem.Body(currentTask))
            }
            elapsedTime >= tomorrowDateTime.toDateTime().millis
              && elapsedTime < twoDaysFromNow.toDateTime().millis -> {
              if (map[tomorrowKey] == null) {
                map[tomorrowKey] = ArrayList()
              }
              map[tomorrowKey]?.add(GenericReminderItem.Body(currentTask))
            }
            elapsedTime >= twoDaysFromNow.toDateTime().millis
              && elapsedTime < nextWeekDateTime.toDateTime().millis -> {
              if (map[thisWeekKey] == null) {
                map[thisWeekKey] = ArrayList()
              }
              map[thisWeekKey]?.add(GenericReminderItem.Body(currentTask))
            }
            else -> {
              if (map[laterKey] == null) {
                map[laterKey] = ArrayList()
              }
              map[laterKey]?.add(GenericReminderItem.Body(currentTask))
            }
          }
        }
      }

      map.forEach { (key, value) ->
        toReturn.add(GenericReminderItem.Header(Task("", "", ""), key))

        value.forEach { item ->
          toReturn.add(item)
        }
      }

      return toReturn
    }
  }
}