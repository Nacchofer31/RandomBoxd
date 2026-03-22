package com.nacchofer31.randomboxd.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DataErrorTest {
    @Test
    fun `DataError Remote entries are all reachable`() {
        val remoteErrors = DataError.Remote.entries
        assertEquals(7, remoteErrors.size)
        assertIs<DataError>(DataError.Remote.REQUEST_TIMEOUT)
        assertIs<DataError>(DataError.Remote.TOO_MANY_REQUESTS)
        assertIs<DataError>(DataError.Remote.NO_INTERNET)
        assertIs<DataError>(DataError.Remote.SERVER)
        assertIs<DataError>(DataError.Remote.SERIALIZATION)
        assertIs<DataError>(DataError.Remote.UNKNOWN)
        assertIs<DataError>(DataError.Remote.NO_RESULTS)
    }

    @Test
    fun `DataError Local DISK_FULL is reachable`() {
        val error = DataError.Local.DISK_FULL
        assertIs<DataError>(error)
        assertEquals(DataError.Local.DISK_FULL, error)
    }

    @Test
    fun `DataError Local UNKNOWN is reachable`() {
        val error = DataError.Local.UNKNOWN
        assertIs<DataError>(error)
        assertEquals(DataError.Local.UNKNOWN, error)
    }

    @Test
    fun `DataError Local entries has two values`() {
        assertEquals(2, DataError.Local.entries.size)
    }
}
