package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RandomFilmScrappingRepositoryTest {
    private val paginationHtml =
        """<ul class="paginate-pages"><li class="paginate-page">1</li></ul>"""

    private val filmListHtml =
        """
        <ul>
            <li class="griditem">
                <div class="react-component" data-postered-identifier='{"lid":"2Ya4","uid":"film:12345","type":"film","typeName":"film"}' data-item-slug="test-film" data-item-name="Test Film (2020)"></div>
            </li>
        </ul>
        """.trimIndent()

    private val alternateFilmListHtml =
        """
        <ul>
            <li class="griditem">
                <div class="react-component" data-postered-identifier='{"lid":"3Zb5","uid":"film:99999","type":"film","typeName":"film"}' data-item-slug="other-film" data-item-name="Other Film (2021)"></div>
            </li>
        </ul>
        """.trimIndent()

    private val filmDetailHtml =
        """
        <html><head>
            <script type="application/ld+json">{"image":"https://example.com/poster.jpg","name":"Test Film"}</script>
        </head></html>
        """.trimIndent()

    private val emptyFilmListHtml = "<ul></ul>"

    private fun createRepository(mockEngine: MockEngine) = RandomFilmScrappingRepository(HttpClient(mockEngine))

    @Test
    fun `getRandomMovies returns film on successful watchlist scraping`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user")

            assertIs<ResultData.Success<*>>(result)
            val films = (result as ResultData.Success).data
            val film = films.first()
            assertEquals("Test Film", film.name)
            assertEquals(2020, film.releaseYear)
        }

    @Test
    fun `getRandomMovies returns NO_RESULTS error when watchlist is empty`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content = if (path.contains("/page/")) emptyFilmListHtml else paginationHtml,
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_RESULTS, (result as ResultData.Error).error)
        }

    @Test
    fun `getRandomMovies with list slash notation returns film`() =
        runTest {
            val listFilmHtml =
                """
                <ul>
                    <li class="posteritem">
                        <div class="react-component" data-postered-identifier='{"lid":"2Ya4","uid":"film:12345","type":"film","typeName":"film"}' data-item-slug="test-film" data-item-name="Test Film (2020)"></div>
                    </li>
                </ul>
                """.trimIndent()
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> listFilmHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user/my-list")

            assertIs<ResultData.Success<*>>(result)
            assertNotNull((result as ResultData.Success).data)
        }

    @Test
    fun `getRandomMoviesFromSearchList UNION returns film from any user`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result =
                repository.getRandomMoviesFromSearchList(
                    searchList = setOf("user1", "user2"),
                    filmSearchMode = FilmSearchMode.UNION,
                )

            assertIs<ResultData.Success<*>>(result)
            assertNotNull((result as ResultData.Success).data)
        }

    @Test
    fun `getRandomMoviesFromSearchList INTERSECTION with common films returns film`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result =
                repository.getRandomMoviesFromSearchList(
                    searchList = setOf("user1", "user2"),
                    filmSearchMode = FilmSearchMode.INTERSECTION,
                )

            assertIs<ResultData.Success<*>>(result)
            assertNotNull((result as ResultData.Success).data)
        }

    @Test
    fun `getRandomMoviesFromSearchList INTERSECTION with no common films returns NO_RESULTS error`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content =
                            when {
                                path.contains("/page/") && path.contains("user1") -> filmListHtml
                                path.contains("/page/") && path.contains("user2") -> alternateFilmListHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result =
                repository.getRandomMoviesFromSearchList(
                    searchList = setOf("user1", "user2"),
                    filmSearchMode = FilmSearchMode.INTERSECTION,
                )

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_RESULTS, (result as ResultData.Error).error)
        }

    @Test
    fun `getRandomMoviesFromSearchList with empty search list returns error`() =
        runTest {
            val mockEngine = MockEngine { _ -> respond("", HttpStatusCode.OK) }
            val repository = createRepository(mockEngine)

            val result =
                repository.getRandomMoviesFromSearchList(
                    searchList = emptySet(),
                    filmSearchMode = FilmSearchMode.UNION,
                )

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.SERIALIZATION, (result as ResultData.Error).error)
        }

    @Test
    fun `getRandomMovie returns error when HTTP request fails`() =
        runTest {
            val mockEngine = MockEngine { _ -> respond("", HttpStatusCode.InternalServerError) }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.SERVER, (result as ResultData.Error).error)
        }

    @Test
    fun `getRandomMoviesFromSearchList returns error when HTTP request fails`() =
        runTest {
            val mockEngine = MockEngine { _ -> respond("", HttpStatusCode.InternalServerError) }
            val repository = createRepository(mockEngine)

            val result =
                repository.getRandomMoviesFromSearchList(
                    searchList = setOf("user1"),
                    filmSearchMode = FilmSearchMode.UNION,
                )

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.SERVER, (result as ResultData.Error).error)
        }

    @Test
    fun `getRandomMovie uses film imageUrl when poster page request fails`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    if (path.startsWith("/film/")) {
                        respond("", HttpStatusCode.InternalServerError, headersOf("Content-Type", "text/html"))
                    } else {
                        respond(
                            content = if (path.contains("/page/")) filmListHtml else paginationHtml,
                            status = HttpStatusCode.OK,
                            headers = headersOf("Content-Type", "text/html"),
                        )
                    }
                }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user")

            // Even when poster fails, should succeed using the fallback imageUrl
            assertIs<ResultData.Success<*>>(result)
        }

    @Test
    fun `getRandomMovie uses film imageUrl when poster page has no script tag`() =
        runTest {
            val filmDetailNoScriptHtml = "<html><head><title>Film</title></head><body></body></html>"
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailNoScriptHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user")

            assertIs<ResultData.Success<*>>(result)
            // imageUrl should fall back to the one built from filmId
            val films = (result as ResultData.Success).data
            assertNotNull(films.first().imageUrl)
        }

    @Test
    fun `getRandomMovie with genre filter appends genre slug to watchlist url`() =
        runTest {
            val requestedPaths = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    requestedPaths.add(path)
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            repository.getRandomMovies("user", setOf(FilmGenre.ACTION))

            assertTrue(requestedPaths.any { it.contains("/genre/action") })
        }

    @Test
    fun `getRandomMovie with multiple genres appends plus-separated slugs to watchlist url`() =
        runTest {
            val requestedPaths = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    requestedPaths.add(path)
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            repository.getRandomMovies("user", setOf(FilmGenre.ACTION, FilmGenre.HORROR))

            assertTrue(requestedPaths.any { path -> path.contains("/genre/") && path.contains("action") && path.contains("horror") })
        }

    @Test
    fun `getRandomMovie with no genres uses base watchlist url without genre segment`() =
        runTest {
            val requestedPaths = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    requestedPaths.add(path)
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            repository.getRandomMovies("user", emptySet())

            assertTrue(requestedPaths.none { it.contains("/genre/") })
        }

    @Test
    fun `getRandomMoviesFromSearchList with genre filter appends genre slug to urls`() =
        runTest {
            val requestedPaths = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    requestedPaths.add(path)
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> filmListHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            repository.getRandomMoviesFromSearchList(
                searchList = setOf("user1"),
                filmSearchMode = FilmSearchMode.UNION,
                selectedGenres = setOf(FilmGenre.DRAMA),
            )

            assertTrue(requestedPaths.any { it.contains("/genre/drama") })
        }

    @Test
    fun `getRandomMovie with list notation and genre filter appends genre to list url`() =
        runTest {
            val listFilmHtml =
                """
                <ul>
                    <li class="posteritem">
                        <div class="react-component" data-postered-identifier='{"lid":"2Ya4","uid":"film:12345","type":"film","typeName":"film"}' data-item-slug="test-film" data-item-name="Test Film (2020)"></div>
                    </li>
                </ul>
                """.trimIndent()
            val requestedPaths = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    requestedPaths.add(path)
                    respond(
                        content =
                            when {
                                path.contains("/page/") -> listFilmHtml
                                path.startsWith("/film/") -> filmDetailHtml
                                else -> paginationHtml
                            },
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            repository.getRandomMovies("user/my-list", setOf(FilmGenre.COMEDY))

            assertTrue(requestedPaths.any { it.contains("/genre/comedy") })
        }

    @Test
    fun `getRandomMovie handles total pages request failure`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    if (path.contains("/watchlist") && !path.contains("/page/")) {
                        // Initial request for pagination fails - fallback to 1 page
                        respond("", HttpStatusCode.InternalServerError)
                    } else if (path.contains("/page/")) {
                        // Page request succeeds
                        respond(
                            content = filmListHtml,
                            status = HttpStatusCode.OK,
                            headers = headersOf("Content-Type", "text/html"),
                        )
                    } else if (path.startsWith("/film/")) {
                        respond(
                            content = filmDetailHtml,
                            status = HttpStatusCode.OK,
                            headers = headersOf("Content-Type", "text/html"),
                        )
                    } else {
                        respond("", HttpStatusCode.InternalServerError)
                    }
                }
            val repository = createRepository(mockEngine)

            val result = repository.getRandomMovies("user")

            // Should fallback to 1 page and continue processing successfully
            assertIs<ResultData.Success<*>>(result)
            assertNotNull((result as ResultData.Success).data)
        }

    @Test
    fun `extractResultMovie returns film with updated imageUrl from poster page`() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    val path = request.url.encodedPath
                    respond(
                        content = if (path.startsWith("/film/")) filmDetailHtml else paginationHtml,
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)
            val film =
                com.nacchofer31.randomboxd.random_film.domain.model.Film(
                    slug = "test-film",
                    imageUrl = "https://fallback.com/poster.jpg",
                    releaseYear = 2020,
                    name = "Test Film",
                )

            val result = repository.extractResultMovie(film)

            assertIs<ResultData.Success<*>>(result)
            val resultFilm = (result as ResultData.Success).data
            assertEquals("https://example.com/poster.jpg", resultFilm.imageUrl)
            assertEquals("Test Film", resultFilm.name)
        }

    @Test
    fun `extractResultMovie falls back to original imageUrl when poster page fails`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond("", HttpStatusCode.InternalServerError, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)
            val film =
                com.nacchofer31.randomboxd.random_film.domain.model.Film(
                    slug = "test-film",
                    imageUrl = "https://fallback.com/poster.jpg",
                    releaseYear = 2020,
                    name = "Test Film",
                )

            val result = repository.extractResultMovie(film)

            assertIs<ResultData.Success<*>>(result)
            val resultFilm = (result as ResultData.Success).data
            assertEquals("https://fallback.com/poster.jpg", resultFilm.imageUrl)
        }
}
