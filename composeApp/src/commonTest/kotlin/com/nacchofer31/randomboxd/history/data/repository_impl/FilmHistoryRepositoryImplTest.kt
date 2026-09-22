package com.nacchofer31.randomboxd.history.data.repository_impl

import app.cash.turbine.test
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryEntry
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.utils.fakes.FakeFilmHistoryDao
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FilmHistoryRepositoryImplTest {
    private val dao = FakeFilmHistoryDao()

    private val fixedInstant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
    private val clock: Clock =
        object : Clock {
            override fun now(): Instant = fixedInstant
        }

    private lateinit var repository: FilmHistoryRepository

    private fun createRepository() {
        repository = FilmHistoryRepositoryImpl(dao, clock)
    }

    private val testFilm =
        Film(
            slug = "https://letterboxd.com/film/the-matrix/",
            imageUrl = "https://example.com/poster.jpg",
            releaseYear = 1999,
            name = "The Matrix",
        )

    @Test
    fun `save inserts entity with timestamp from injected Clock`() =
        runTest {
            createRepository()

            repository.save(testFilm, setOf("alice"), FilmSearchMode.UNION, setOf(FilmGenre.ACTION))

            // The entry is captured by the fake DAO and encoded from the injected Clock.
            assertEquals(1, dao.insertedEntries.size)
            assertEquals(fixedInstant.toEpochMilliseconds(), dao.insertedEntries.single().timestamp)
        }

    @Test
    fun `save with null releaseYear inserts null`() =
        runTest {
            val noYearFilm = Film(slug = "slug", imageUrl = "url", releaseYear = null, name = "Film")
            createRepository()

            repository.save(noYearFilm, setOf("bob"), FilmSearchMode.INTERSECTION, emptySet())

            assertEquals(null, dao.insertedEntries.single().releaseYear)
        }

    @Test
    fun `getAllPicks maps entities to FilmPick ordered by timestamp DESC`() =
        runTest {
            val earlierEntry =
                FilmHistoryEntry(
                    id = 1,
                    filmSlug = "a",
                    filmName = "A",
                    posterUrl = "",
                    releaseYear = null,
                    userNames = "x",
                    searchMode = "UNION",
                    selectedGenres = "",
                    timestamp = 1000,
                    isFavorite = false,
                )
            val laterEntry =
                FilmHistoryEntry(
                    id = 2,
                    filmSlug = "b",
                    filmName = "B",
                    posterUrl = "",
                    releaseYear = null,
                    userNames = "y",
                    searchMode = "INTERSECTION",
                    selectedGenres = "",
                    timestamp = 2000,
                    isFavorite = true,
                )
            dao.picks = listOf(laterEntry, earlierEntry)
            createRepository()

            repository.getAllPicks().test {
                val picks = awaitItem()
                assertEquals(2, picks.size)
                assertEquals("b", picks[0].filmSlug)
                assertEquals("a", picks[1].filmSlug)
                assertEquals(true, picks[0].isFavorite)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updateFavorite delegates to DAO`() =
        runTest {
            createRepository()

            repository.updateFavorite(42, true)

            assertEquals(listOf(42 to true), dao.updateFavoriteCalls)
        }

    @Test
    fun `deleteAll delegates to DAO`() =
        runTest {
            createRepository()

            repository.deleteAll()

            assertEquals(1, dao.deleteAllCount)
        }

    @Test
    fun `getAllPicks empty list maps correctly`() =
        runTest {
            dao.picks = emptyList()
            createRepository()

            repository.getAllPicks().test {
                val picks = awaitItem()
                assertTrue(picks.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
