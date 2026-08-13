package com.nacchofer31.randomboxd.history.presentation.components

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

sealed class TimestampDisplay {
    data class Today(
        val time: String,
    ) : TimestampDisplay()

    data class Yesterday(
        val time: String,
    ) : TimestampDisplay()

    data class DaysAgo(
        val days: Int,
    ) : TimestampDisplay()
}

@OptIn(ExperimentalTime::class)
fun formatPickTimestamp(
    instant: Instant,
    now: Instant,
    timeZone: TimeZone,
): TimestampDisplay {
    val localDateTime = instant.toLocalDateTime(timeZone)
    val nowLocal = now.toLocalDateTime(timeZone)
    val timeStr = formatTwoDigit(localDateTime.hour) + ":" + formatTwoDigit(localDateTime.minute)
    val dayDiff = (nowLocal.date.toEpochDays() - localDateTime.date.toEpochDays()).toInt()
    return when (dayDiff) {
        0 -> TimestampDisplay.Today(timeStr)
        1 -> TimestampDisplay.Yesterday(timeStr)
        else -> TimestampDisplay.DaysAgo(dayDiff)
    }
}

private fun formatTwoDigit(value: Int): String = if (value < 10) "0$value" else value.toString()
