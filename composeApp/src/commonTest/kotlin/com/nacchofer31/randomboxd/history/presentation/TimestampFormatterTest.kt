package com.nacchofer31.randomboxd.history.presentation

import com.nacchofer31.randomboxd.history.presentation.components.TimestampDisplay
import com.nacchofer31.randomboxd.history.presentation.components.formatPickTimestamp
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class TimestampFormatterTest {
    private val tz = TimeZone.UTC

    // Friday, 25 October 2024 14:30:00 UTC
    private val fixedInstant = Instant.fromEpochMilliseconds(1_729_866_600_000L)

    @Test
    fun `same local date formats as Today`() {
        // now = same instant → same day
        val result = formatPickTimestamp(fixedInstant, fixedInstant, tz)
        assertEquals(TimestampDisplay.Today::class, result::class)
        val today = result as TimestampDisplay.Today
        assertEquals("14:30", today.time)
    }

    @Test
    fun `same date in later hours formats as Today`() {
        val later = Instant.fromEpochMilliseconds(1_729_866_600_000L + 3_600_000) // +1h
        val result = formatPickTimestamp(fixedInstant, later, tz)
        assertTrue(result is TimestampDisplay.Today)
    }

    @Test
    fun `one day before formats as Yesterday`() {
        val now = Instant.fromEpochMilliseconds(1_729_866_600_000L + 86_400_000) // +24h
        val result = formatPickTimestamp(fixedInstant, now, tz)
        assertTrue(result is TimestampDisplay.Yesterday)
        val yesterday = result as TimestampDisplay.Yesterday
        assertEquals("14:30", yesterday.time)
    }

    @Test
    fun `two days before formats as DaysAgo`() {
        val now = Instant.fromEpochMilliseconds(1_729_866_600_000L + 2 * 86_400_000) // +48h
        val result = formatPickTimestamp(fixedInstant, now, tz)
        assertTrue(result is TimestampDisplay.DaysAgo)
        val daysAgo = result as TimestampDisplay.DaysAgo
        assertEquals(2, daysAgo.days)
    }

    @Test
    fun `multiple days before formats as DaysAgo`() {
        val now = Instant.fromEpochMilliseconds(1_729_866_600_000L + 5 * 86_400_000) // +120h
        val result = formatPickTimestamp(fixedInstant, now, tz)
        assertTrue(result is TimestampDisplay.DaysAgo)
        assertEquals(5, (result as TimestampDisplay.DaysAgo).days)
    }

    @Test
    fun `HHmm is zero-padded`() {
        val earlyInstant = Instant.fromEpochMilliseconds(1_729_847_460_000L) // 09:11 UTC
        val result = formatPickTimestamp(earlyInstant, earlyInstant, tz)
        assertTrue(result is TimestampDisplay.Today)
        assertEquals("09:11", (result as TimestampDisplay.Today).time)
    }

    @Test
    fun `midnight formats correctly`() {
        val midnight = Instant.fromEpochMilliseconds(1_729_728_000_000L) // Oct 25 2024 00:00 UTC
        val result = formatPickTimestamp(midnight, midnight, tz)
        assertTrue(result is TimestampDisplay.Today)
        assertEquals("00:00", (result as TimestampDisplay.Today).time)
    }

    @Test
    fun `timezone boundary changes day label`() {
        // 2024-10-25 23:00 UTC → in UTC+2 this is 2024-10-26 01:00
        val entry = Instant.fromEpochMilliseconds(1_729_897_200_000L) // 23:00 UTC
        val now = Instant.fromEpochMilliseconds(1_729_897_200_000L + 3_600_000) // 00:00 UTC next day
        val utcResult = formatPickTimestamp(entry, now, TimeZone.UTC)
        // In UTC: entry is Oct 25, now is Oct 26 → 1 day difference → Yesterday
        assertTrue(utcResult is TimestampDisplay.Yesterday)

        // In UTC+2: entry is Oct 26, now is Oct 26 → same day → Today
        val plus2Result = formatPickTimestamp(entry, now, TimeZone.of("UTC+2"))
        assertTrue(plus2Result is TimestampDisplay.Today)
    }
}
