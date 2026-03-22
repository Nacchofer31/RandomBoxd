package com.nacchofer31.randomboxd.core.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultDispatchersTest {
    private val dispatchers = DefaultDispatchers()

    @Test
    fun `io dispatcher returns Dispatchers IO`() {
        assertEquals(Dispatchers.IO, dispatchers.io)
    }

    @Test
    fun `default dispatcher returns Dispatchers Default`() {
        assertEquals(Dispatchers.Default, dispatchers.default)
    }
}
