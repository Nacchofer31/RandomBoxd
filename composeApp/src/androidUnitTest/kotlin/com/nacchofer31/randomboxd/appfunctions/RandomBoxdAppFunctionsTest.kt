package com.nacchofer31.randomboxd.appfunctions

import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.generated.mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// AppFunctionException error-path tests are intentionally omitted: the library's
// exception constructors dereference Android framework constants (e.g. Bundle.EMPTY)
// that are null in plain JVM unit tests. Verify the error mapping via instrumented
// tests or Robolectric in a follow-up.
class RandomBoxdAppFunctionsTest : TestsWithMocks() {
    @Mock lateinit var repository: RandomFilmRepository

    private lateinit var appFunctions: RandomBoxdAppFunctions

    private val sampleFilm =
        Film(
            slug = "the-witch",
            imageUrl = "https://example.com/poster.jpg",
            releaseYear = 2015,
            name = "The Witch",
        )

    override fun setUpMocks() {
        repository = mocker.mock<RandomFilmRepository>()
        appFunctions = RandomBoxdAppFunctions(repository)
    }

    @Test
    fun `pickRandomMovie maps successful repository result to MovieResult`() =
        runTest {
            mocker.everySuspending { repository.getRandomMovie(isAny(), isAny()) } returns ResultData.Success(sampleFilm)

            val result =
                appFunctions.pickRandomMovieInternal(
                    RandomBoxdAppFunctions.PickRandomMovieParams(userName = "nachofer31"),
                )

            assertEquals("The Witch", result.title)
            assertEquals(2015, result.year)
            assertEquals("https://letterboxd.com/film/the-witch/", result.letterboxdUrl)
            assertEquals("https://example.com/poster.jpg", result.posterUrl)
        }

    @Test
    fun `parseGenres maps known slugs to FilmGenre and drops unknown ones`() {
        val result = appFunctions.parseGenres(listOf("horror", "comedy", "not-a-real-genre"))

        assertEquals(setOf(FilmGenre.HORROR, FilmGenre.COMEDY), result)
    }

    @Test
    fun `parseGenres returns empty set for null input`() {
        assertTrue(appFunctions.parseGenres(null).isEmpty())
    }

    @Test
    fun `parseGenres returns empty set for empty list`() {
        assertTrue(appFunctions.parseGenres(emptyList()).isEmpty())
    }

    @Test
    fun `pickRandomMovie forwards parsed genres to repository`() =
        runTest {
            mocker.everySuspending { repository.getRandomMovie(isAny(), isAny()) } returns ResultData.Success(sampleFilm)

            appFunctions.pickRandomMovieInternal(
                RandomBoxdAppFunctions.PickRandomMovieParams(
                    userName = "nachofer31",
                    genres = listOf("horror", "science-fiction"),
                ),
            )

            mocker.verifyWithSuspend {
                repository.getRandomMovie("nachofer31", setOf(FilmGenre.HORROR, FilmGenre.SCIENCE_FICTION))
            }
        }

    @Test
    fun `pickRandomMovieFromUsers passes through cleaned usernames and genres`() =
        runTest {
            mocker.everySuspending {
                repository.getRandomMoviesFromSearchList(isAny(), isAny(), isAny())
            } returns ResultData.Success(sampleFilm)

            val result =
                appFunctions.pickRandomMovieFromUsersInternal(
                    RandomBoxdAppFunctions.PickRandomMovieFromUsersParams(
                        userNames = listOf("nachofer31", "  maria  ", "", "juan"),
                        mode = "UNION",
                        genres = listOf("horror"),
                    ),
                )

            assertEquals("The Witch", result.title)
            mocker.verifyWithSuspend {
                repository.getRandomMoviesFromSearchList(
                    setOf("nachofer31", "maria", "juan"),
                    FilmSearchMode.UNION,
                    setOf(FilmGenre.HORROR),
                )
            }
        }

    @Test
    fun `pickRandomMovieFromUsers defaults to INTERSECTION mode`() =
        runTest {
            mocker.everySuspending {
                repository.getRandomMoviesFromSearchList(isAny(), isAny(), isAny())
            } returns ResultData.Success(sampleFilm)

            appFunctions.pickRandomMovieFromUsersInternal(
                RandomBoxdAppFunctions.PickRandomMovieFromUsersParams(
                    userNames = listOf("nachofer31", "maria"),
                ),
            )

            mocker.verifyWithSuspend {
                repository.getRandomMoviesFromSearchList(
                    setOf("nachofer31", "maria"),
                    FilmSearchMode.INTERSECTION,
                    emptySet(),
                )
            }
        }

    @Test
    fun `parseMode accepts case-insensitive INTERSECTION and UNION`() {
        assertEquals(FilmSearchMode.INTERSECTION, appFunctions.parseMode("intersection"))
        assertEquals(FilmSearchMode.INTERSECTION, appFunctions.parseMode("Intersection"))
        assertEquals(FilmSearchMode.UNION, appFunctions.parseMode("union"))
        assertEquals(FilmSearchMode.UNION, appFunctions.parseMode("  UNION  "))
    }

    @Test
    fun `genreSlugConstantsStayInSyncWithFilmGenre`() {
        val constants =
            setOf(
                GenreSlugs.ACTION,
                GenreSlugs.ADVENTURE,
                GenreSlugs.ANIMATION,
                GenreSlugs.COMEDY,
                GenreSlugs.CRIME,
                GenreSlugs.DOCUMENTARY,
                GenreSlugs.DRAMA,
                GenreSlugs.FAMILY,
                GenreSlugs.FANTASY,
                GenreSlugs.HISTORY,
                GenreSlugs.HORROR,
                GenreSlugs.MUSIC,
                GenreSlugs.MYSTERY,
                GenreSlugs.ROMANCE,
                GenreSlugs.SCIENCE_FICTION,
                GenreSlugs.THRILLER,
                GenreSlugs.TV_MOVIE,
                GenreSlugs.WAR,
                GenreSlugs.WESTERN,
            )
        val fromEnum = FilmGenre.entries.map { it.slug }.toSet()
        assertEquals(
            fromEnum,
            constants,
            "GenreSlugs constants drifted from FilmGenre — update both the constants " +
                "and the @AppFunctionStringValueConstraint(enumValues = [...]) lists in " +
                "RandomBoxdAppFunctions.kt",
        )
    }
}
