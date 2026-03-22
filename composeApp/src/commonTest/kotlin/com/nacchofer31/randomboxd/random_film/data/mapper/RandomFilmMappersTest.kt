package com.nacchofer31.randomboxd.random_film.data.mapper

import com.nacchofer31.randomboxd.random_film.data.dto.FilmDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RandomFilmMappersTest {
    @Test
    fun `toFilm maps all fields correctly`() {
        val dto = FilmDto(
            slug = "test-film",
            imageUrl = "https://example.com/poster.jpg",
            releaseYear = "2020",
            name = "Test Film",
        )

        val film = dto.toFilm()

        assertEquals("test-film", film.slug)
        assertEquals("https://example.com/poster.jpg", film.imageUrl)
        assertEquals(2020, film.releaseYear)
        assertEquals("Test Film", film.name)
    }

    @Test
    fun `toFilm with empty releaseYear maps to null`() {
        val dto = FilmDto(
            slug = "no-year-film",
            imageUrl = "https://example.com/poster.jpg",
            releaseYear = "",
            name = "No Year Film",
        )

        val film = dto.toFilm()

        assertNull(film.releaseYear)
    }

    @Test
    fun `toFilm preserves slug exactly`() {
        val dto = FilmDto(
            slug = "my-special-film-2024",
            imageUrl = "",
            releaseYear = "1994",
            name = "Some Film",
        )

        val film = dto.toFilm()

        assertEquals("my-special-film-2024", film.slug)
        assertEquals(1994, film.releaseYear)
    }
}
