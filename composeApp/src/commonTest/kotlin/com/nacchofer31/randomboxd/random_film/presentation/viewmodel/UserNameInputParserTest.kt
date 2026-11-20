package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserNameInputParserTest {
    @Test
    fun `plain username has no list user`() {
        val parsed = ParsedUserNameInput.from("alice")

        assertNull(parsed.listUser)
        assertEquals("", parsed.listQuery)
    }

    @Test
    fun `empty text has no list user`() {
        val parsed = ParsedUserNameInput.from("")

        assertNull(parsed.listUser)
        assertEquals("", parsed.listQuery)
    }

    @Test
    fun `leading slash has no list user`() {
        val parsed = ParsedUserNameInput.from("/alice")

        assertNull(parsed.listUser)
        assertEquals("alice", parsed.listQuery)
    }

    @Test
    fun `trailing slash returns the user and an empty query`() {
        val parsed = ParsedUserNameInput.from("alice/")

        assertEquals("alice", parsed.listUser)
        assertEquals("", parsed.listQuery)
    }

    @Test
    fun `user and query after the slash`() {
        val parsed = ParsedUserNameInput.from("alice/que")

        assertEquals("alice", parsed.listUser)
        assertEquals("que", parsed.listQuery)
    }

    @Test
    fun `user is trimmed`() {
        val parsed = ParsedUserNameInput.from("  alice  /que")

        assertEquals("alice", parsed.listUser)
        assertEquals("que", parsed.listQuery)
    }

    @Test
    fun `second slash means a new user is being typed`() {
        val parsed = ParsedUserNameInput.from("alice/bob/")

        assertEquals("bob", parsed.listUser)
        assertEquals("", parsed.listQuery)
    }

    @Test
    fun `second slash with query uses the new user`() {
        val parsed = ParsedUserNameInput.from("alice/bob/que")

        assertEquals("bob", parsed.listUser)
        assertEquals("que", parsed.listQuery)
    }

    @Test
    fun `blank middle segment has no list user`() {
        val parsed = ParsedUserNameInput.from("alice//que")

        assertNull(parsed.listUser)
        assertEquals("que", parsed.listQuery)
    }

    @Test
    fun `query keeps slashes typed after the second separator`() {
        val parsed = ParsedUserNameInput.from("alice/bob/que/x")

        assertEquals("bob", parsed.listUser)
        assertEquals("que/x", parsed.listQuery)
    }

    @Test
    fun `selected list value parses back to the same user`() {
        val parsed = ParsedUserNameInput.from("bob/horror-night")

        assertEquals("bob", parsed.listUser)
        assertEquals("horror-night", parsed.listQuery)
    }
}
