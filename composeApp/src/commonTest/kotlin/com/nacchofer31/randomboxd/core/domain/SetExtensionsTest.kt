package com.nacchofer31.randomboxd.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SetExtensionsTest {
    data class TestItem(
        val id: Int,
        val name: String,
    )

    @Test
    fun `randomExcluding throws NoSuchElementException when set is empty`() {
        val emptySet = emptySet<TestItem>()

        assertFailsWith<NoSuchElementException> {
            emptySet.randomExcluding(keySelector = { it.id })
        }
    }

    @Test
    fun `randomExcluding returns random element when exclude is null`() {
        val set = setOf(TestItem(1, "one"), TestItem(2, "two"), TestItem(3, "three"))

        val result = set.randomExcluding(exclude = null, keySelector = { it.id })

        assertTrue(result in set)
    }

    @Test
    fun `randomExcluding returns element different from excluded using keySelector`() {
        val excluded = TestItem(2, "two")
        val set = setOf(TestItem(1, "one"), excluded, TestItem(3, "three"))

        repeat(10) {
            val result = set.randomExcluding(exclude = excluded, keySelector = { it.id })
            assertTrue(result.id != excluded.id)
        }
    }

    @Test
    fun `randomExcluding returns excluded element when it is the only one`() {
        val onlyItem = TestItem(1, "one")
        val set = setOf(onlyItem)

        val result = set.randomExcluding(exclude = onlyItem, keySelector = { it.id })

        assertEquals(onlyItem, result)
    }

    @Test
    fun `randomExcluding uses keySelector for comparison not object equality`() {
        val excluded = TestItem(1, "original")
        val differentObject = TestItem(1, "different")
        val set = setOf(excluded, differentObject, TestItem(2, "two"))

        repeat(10) {
            val result = set.randomExcluding(exclude = excluded, keySelector = { it.id })
            assertTrue(result.id != 1, "Should exclude all items with id=1")
        }
    }

    @Test
    fun `randomExcluding works with single element set and null exclude`() {
        val item = TestItem(1, "one")
        val set = setOf(item)

        val result = set.randomExcluding(exclude = null, keySelector = { it.id })

        assertEquals(item, result)
    }
}
