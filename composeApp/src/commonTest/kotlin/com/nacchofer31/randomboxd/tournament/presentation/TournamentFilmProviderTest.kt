package com.nacchofer31.randomboxd.tournament.presentation

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class TournamentFilmProviderTest {
    private val champion =
        Film(
            slug = "champion",
            imageUrl = "https://example.com/champion.jpg",
            releaseYear = 2020,
            name = "Champion",
        )

    private val runnerUp =
        Film(
            slug = "runner-up",
            imageUrl = "https://example.com/runner-up.jpg",
            releaseYear = 2021,
            name = "Runner Up",
        )

    @Test
    fun `given a fresh provider then there is no champion to consume`() {
        val provider = TournamentFilmProvider()

        assertNull(provider.champion.value)
        assertNull(provider.consumeChampion())
    }

    @Test
    fun `given a published champion when consumed then the same film comes back and the slot is cleared`() {
        val provider = TournamentFilmProvider()

        provider.publishChampion(champion)

        assertSame(champion, provider.champion.value)
        assertSame(champion, provider.consumeChampion())
        assertNull(provider.champion.value)
    }

    @Test
    fun `given a consumed champion when consumed again then nothing is applied a second time`() {
        val provider = TournamentFilmProvider()

        provider.publishChampion(champion)
        provider.consumeChampion()

        assertNull(provider.consumeChampion())
        assertNull(provider.champion.value)
    }

    @Test
    fun `given a consumed champion when a new tournament publishes another one then the latest wins`() {
        val provider = TournamentFilmProvider()

        provider.publishChampion(champion)
        provider.consumeChampion()
        provider.publishChampion(runnerUp)

        assertSame(runnerUp, provider.consumeChampion())
        assertNull(provider.consumeChampion())
    }

    @Test
    fun `given two published champions then only the most recent is consumed`() {
        val provider = TournamentFilmProvider()

        provider.publishChampion(champion)
        provider.publishChampion(runnerUp)

        assertSame(runnerUp, provider.consumeChampion())
    }

    @Test
    fun `publishing and consuming a champion keeps the pending tournament context untouched`() {
        val films = listOf(champion, runnerUp)
        val provider =
            TournamentFilmProvider().apply {
                pendingFilms = films
                pendingUserNames = setOf("user1")
                pendingSearchMode = FilmSearchMode.UNION
                pendingGenres = setOf(FilmGenre.ACTION)
            }

        provider.publishChampion(champion)
        provider.consumeChampion()

        assertEquals(films, provider.pendingFilms)
        assertEquals(setOf("user1"), provider.pendingUserNames)
        assertEquals(FilmSearchMode.UNION, provider.pendingSearchMode)
        assertEquals(setOf(FilmGenre.ACTION), provider.pendingGenres)
    }
}
