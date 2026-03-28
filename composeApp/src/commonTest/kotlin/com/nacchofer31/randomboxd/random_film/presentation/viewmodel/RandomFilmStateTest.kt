package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RandomFilmStateTest {
    @Test
    fun `default state has empty userName`() {
        val state = RandomFilmState()

        assertEquals("", state.userName)
    }

    @Test
    fun `default state has null resultFilm`() {
        val state = RandomFilmState()

        assertNull(state.resultFilm)
    }

    @Test
    fun `default state has null resultError`() {
        val state = RandomFilmState()

        assertNull(state.resultError)
    }

    @Test
    fun `default state is not loading`() {
        val state = RandomFilmState()

        assertEquals(false, state.isLoading)
    }

    @Test
    fun `default state has empty userNameSearchList`() {
        val state = RandomFilmState()

        assertEquals(emptySet<String>(), state.userNameSearchList)
    }

    @Test
    fun `default state has INTERSECTION filmSearchMode`() {
        val state = RandomFilmState()

        assertEquals(FilmSearchMode.INTERSECTION, state.filmSearchMode)
    }

    @Test
    fun `copy with resultFilm updates film`() {
        val film = Film(slug = "test", imageUrl = "url", name = "Test Film")
        val state = RandomFilmState().copy(resultFilm = film)

        assertEquals(film, state.resultFilm)
    }

    @Test
    fun `copy with isLoading true sets loading state`() {
        val state = RandomFilmState().copy(isLoading = true)

        assertEquals(true, state.isLoading)
    }

    @Test
    fun `copy with resultError updates error`() {
        val state = RandomFilmState().copy(resultError = DataError.Remote.NO_RESULTS)

        assertEquals(DataError.Remote.NO_RESULTS, state.resultError)
    }

    @Test
    fun `copy with userNameSearchList updates list`() {
        val state = RandomFilmState().copy(userNameSearchList = setOf("user1", "user2"))

        assertEquals(setOf("user1", "user2"), state.userNameSearchList)
    }

    @Test
    fun `copy with UNION filmSearchMode updates mode`() {
        val state = RandomFilmState().copy(filmSearchMode = FilmSearchMode.UNION)

        assertEquals(FilmSearchMode.UNION, state.filmSearchMode)
    }
}
