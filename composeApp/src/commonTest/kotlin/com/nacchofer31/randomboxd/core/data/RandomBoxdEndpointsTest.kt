package com.nacchofer31.randomboxd.core.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RandomBoxdEndpointsTest {
    @Test
    fun `getUserRandomFilm returns expected api url`() {
        val result = RandomBoxdEndpoints.getUserRandomFilm("testuser")
        assertEquals("/api?users=testuser", result)
    }

    @Test
    fun `getUserNameWatchlist returns expected letterboxd url`() {
        val result = RandomBoxdEndpoints.getUserNameWatchlist("testuser")
        assertEquals("https://letterboxd.com/testuser/watchlist", result)
    }

    @Test
    fun `getUserNameFromList returns expected list url`() {
        val result = RandomBoxdEndpoints.getUserNameFromList("testuser", "my-list")
        assertEquals("https://letterboxd.com/testuser/list/my-list", result)
    }

    @Test
    fun `filmSlugUrl returns expected film url`() {
        val result = RandomBoxdEndpoints.filmSlugUrl("test-film")
        assertEquals("https://letterboxd.com/film/test-film/", result)
    }

    @Test
    fun `filmPosterUrl returns expected poster url`() {
        val result = RandomBoxdEndpoints.filmPosterUrl("1/2/3/4/5/", "12345", "test-film")
        assertTrue(result.startsWith("https://a.ltrbxd.com/resized/film-poster/"))
        assertTrue(result.contains("12345"))
        assertTrue(result.contains("test-film"))
    }
}
