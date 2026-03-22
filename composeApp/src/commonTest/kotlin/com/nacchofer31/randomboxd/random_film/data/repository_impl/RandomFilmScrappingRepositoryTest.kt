package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
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

class RandomFilmScrappingRepositoryTest {
    private val paginationHtml =
        """<ul class="paginate-pages"><li class="paginate-page">1</li></ul>"""

    private val filmListHtml =
        """
        <ul>
            <li class="griditem">
                <div class="react-component" data-film-id="12345" data-item-slug="test-film" data-item-name="Test Film (2020)"></div>
            </li>
        </ul>
        """.trimIndent()

    private val alternateFilmListHtml =
        """
        <ul>
            <li class="griditem">
                <div class="react-component" data-film-id="99999" data-item-slug="other-film" data-item-name="Other Film (2021)"></div>
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
    fun `getRandomMovie returns film on successful watchlist scraping`() =
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

            val result = repository.getRandomMovie("user")

            assertIs<ResultData.Success<*>>(result)
            val film = (result as ResultData.Success).data
            assertEquals("Test Film", film.name)
            assertEquals(2020, film.releaseYear)
            assertEquals("https://example.com/poster.jpg", film.imageUrl)
        }

    @Test
    fun `getRandomMovie returns NO_RESULTS error when watchlist is empty`() =
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

            val result = repository.getRandomMovie("user")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_RESULTS, (result as ResultData.Error).error)
        }

    @Test
    fun `getRandomMovie with list slash notation returns film`() =
        runTest {
            val listFilmHtml =
                """
                <ul>
                    <li class="posteritem">
                        <div class="react-component" data-film-id="12345" data-item-slug="test-film" data-item-name="Test Film (2020)"></div>
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

            val result = repository.getRandomMovie("user/my-list")

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
}
