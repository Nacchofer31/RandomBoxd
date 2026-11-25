package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserListsPage
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

    // ── User lists ───────────────────────────────────────────────────

    private val userListsNextPageHtml =
        """<div class="paginate-nextprev"><a class="next" href="/jack/lists/page/2/">Older</a></div>"""

    private val userListsLastPageHtml =
        """<div class="paginate-nextprev paginate-disabled"><span class="next">Older</span></div>"""

    private val userListPageTwoHtml =
        """
        <div class="listitem">
            <article class="list-summary" data-film-list-id="13" data-person="jack">
                <div class="body">
                    <div class="masthead">
                        <h2 class="name prettify"><a href="/jack/list/late-night-gems/">Late Night Gems</a></h2>
                        <div class="content-reactions-strip -filmlist"><span class="value">12&nbsp;films</span></div>
                    </div>
                </div>
            </article>
        </div>
        """.trimIndent()

    private val userListHtml =
        """
        <div class="listitem">
            <article class="list-summary" data-film-list-id="1" data-person="jack">
                <div class="body">
                    <div class="masthead">
                        <h2 class="name prettify"><a href="/jack/list/100-classics-i-saw-in-2026/">100 Classics I Saw in 2026</a></h2>
                        <div class="content-reactions-strip -filmlist"><span class="value">93&nbsp;films</span></div>
                    </div>
                </div>
            </article>
        </div>
        <div class="listitem">
            <article class="list-summary" data-film-list-id="2" data-person="jack">
                <div class="body">
                    <div class="masthead">
                        <h2 class="name prettify"><a href="/jack/list/horror-night/">Horror Night</a></h2>
                        <div class="content-reactions-strip -filmlist"><span class="value">7&nbsp;films</span></div>
                    </div>
                </div>
            </article>
        </div>
        """.trimIndent()

    private val userListsEmptyHtml =
        """
        <section class="ui-block-header empty-text body-text -small">
            <h2 class="ui-block-heading"><span class="replace-if-you" data-replacement="No lists yet">No lists yet</span></h2>
        </section>
        """.trimIndent()

    private val userListsNotFoundHtml =
        """<html><head><title>Letterboxd - Not Found</title></head><body class="error message-dark"></body></html>"""

    @Test
    fun `getUserLists returns parsed lists on successful scraping`() =
        runTest {
            val requestedUrls = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    requestedUrls += request.url.toString()
                    respond(userListHtml, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            val lists = page.lists
            assertEquals(2, lists.size)
            assertEquals("100 Classics I Saw in 2026", lists[0].name)
            assertEquals("100-classics-i-saw-in-2026", lists[0].slug)
            assertEquals(93, lists[0].filmCount)
            assertEquals("Horror Night", lists[1].name)
            assertEquals("horror-night", lists[1].slug)
            assertEquals(7, lists[1].filmCount)
            assertEquals(listOf("https://letterboxd.com/jack/lists/"), requestedUrls)
        }

    @Test
    fun `getUserLists page one reports more pages when a next anchor exists`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        userListHtml + userListsNextPageHtml,
                        HttpStatusCode.OK,
                        headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack", page = 1)

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(2, page.lists.size)
            assertTrue(page.hasMore)
        }

    @Test
    fun `getUserLists requests the paged url and reports no more pages on the last page`() =
        runTest {
            val requestedUrls = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    requestedUrls += request.url.toString()
                    respond(
                        userListPageTwoHtml + userListsLastPageHtml,
                        HttpStatusCode.OK,
                        headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack", page = 2)

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(1, page.lists.size)
            assertEquals("Late Night Gems", page.lists[0].name)
            assertEquals("late-night-gems", page.lists[0].slug)
            assertEquals(12, page.lists[0].filmCount)
            assertFalse(page.hasMore)
            assertEquals(listOf("https://letterboxd.com/jack/lists/page/2/"), requestedUrls)
        }

    @Test
    fun `getUserLists out of range page returns empty page without more pages`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond("", HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack", page = 9)

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertTrue(page.lists.isEmpty())
            assertFalse(page.hasMore)
        }

    @Test
    fun `getUserLists returns empty list when user has no lists`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond(userListsEmptyHtml, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(emptyList(), page.lists)
            assertFalse(page.hasMore)
        }

    @Test
    fun `getUserLists returns NO_RESULTS error when user does not exist`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond(userListsNotFoundHtml, HttpStatusCode.NotFound, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("ghost")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_RESULTS, (result as ResultData.Error).error)
        }

    @Test
    fun `getUserLists skips items with unparseable slug`() =
        runTest {
            val html =
                """
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/good-list/">Good List</a></h2></article></div>
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/watchlist/">Broken</a></h2></article></div>
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/good-list/">Duplicated</a></h2></article></div>
                """.trimIndent()
            val mockEngine =
                MockEngine { _ ->
                    respond(html, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            val lists = page.lists
            assertEquals(1, lists.size)
            assertEquals("Good List", lists[0].name)
            assertEquals("good-list", lists[0].slug)
            assertEquals(null, lists[0].filmCount)
            assertFalse(page.hasMore)
        }

    // ── User lists: defensive parsing & transport failures ──────────

    @Test
    fun `getUserLists returns SERIALIZATION when every item is unparsable`() =
        runTest {
            val html =
                """
                <div class="listitem"><article class="list-summary"><div class="body"></div></article></div>
                <div class="listitem"><article class="list-summary"><h2 class="other"><span>No anchor here</span></h2></article></div>
                """.trimIndent()
            val mockEngine =
                MockEngine { _ ->
                    respond(html, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.SERIALIZATION, (result as ResultData.Error).error)
        }

    @Test
    fun `getUserLists skips items with blank names`() =
        runTest {
            val html =
                """
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/blank-name/">   </a></h2></article></div>
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/kept-list/">Kept List</a></h2><span class="value">5 films</span></article></div>
                """.trimIndent()
            val mockEngine =
                MockEngine { _ ->
                    respond(html, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(listOf("kept-list"), page.lists.map { it.slug })
        }

    @Test
    fun `getUserLists strips query and fragment from the list href and skips blank slugs`() =
        runTest {
            val html =
                """
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/with-query?foo=1#bar">With Query</a></h2></article></div>
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list//">Blank Slug</a></h2></article></div>
                """.trimIndent()
            val mockEngine =
                MockEngine { _ ->
                    respond(html, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(1, page.lists.size)
            assertEquals("with-query", page.lists[0].slug)
            assertEquals("With Query", page.lists[0].name)
        }

    @Test
    fun `getUserLists keeps film count null when the item has none or it is not numeric`() =
        runTest {
            val html =
                """
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/no-count/">No Count</a></h2></article></div>
                <div class="listitem"><article class="list-summary"><h2 class="name"><a href="/jack/list/odd-count/">Odd Count</a></h2><span class="value">many films</span></article></div>
                """.trimIndent()
            val mockEngine =
                MockEngine { _ ->
                    respond(html, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(listOf(null, null), page.lists.map { it.filmCount })
        }

    @Test
    fun `getUserLists maps a socket timeout to REQUEST_TIMEOUT`() =
        runTest {
            val mockEngine = MockEngine { _ -> throw SocketTimeoutException("timed out") }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.REQUEST_TIMEOUT, (result as ResultData.Error).error)
        }

    @Test
    fun `getUserLists maps an unresolved address to NO_INTERNET`() =
        runTest {
            val mockEngine = MockEngine { _ -> throw UnresolvedAddressException() }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_INTERNET, (result as ResultData.Error).error)
        }

    @Test
    fun `getUserLists maps any other exception to NO_INTERNET`() =
        runTest {
            val mockEngine = MockEngine { _ -> throw IllegalStateException("boom") }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_INTERNET, (result as ResultData.Error).error)
        }

    @Test
    fun `getUserLists maps a server error status to SERVER`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond("oops", HttpStatusCode.InternalServerError, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getUserLists("jack")

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.SERVER, (result as ResultData.Error).error)
        }

    // ── Popular lists ──────────────────────────────────────────────

    private val popularListsHtml =
        """
        <div class="listitem js-listitem">
            <article class="list-summary js-list-summary js-list" data-film-list-id="101" data-person="official">
                <div class="body">
                    <div class="masthead">
                        <h2 class="name prettify"><a href="/official/list/letterboxds-top-500-films/">Letterboxd's Top 500 Films</a></h2>
                        <div class="content-reactions-strip -filmlist"><span class="value">500&#160;films</span></div>
                    </div>
                </div>
            </article>
        </div>
        <div class="listitem js-listitem">
            <article class="list-summary js-list-summary js-list" data-film-list-id="102" data-person="dave">
                <div class="body">
                    <div class="masthead">
                        <h2 class="name prettify"><a href="/dave/list/best-of-the-decade/">Best of the Decade</a></h2>
                        <div class="content-reactions-strip -filmlist"><span class="value">42&#160;films</span></div>
                    </div>
                </div>
            </article>
        </div>
        """.trimIndent()

    private val popularListSecondPageHtml =
        """
        <div class="listitem js-listitem">
            <article class="list-summary js-list-summary js-list" data-film-list-id="103" data-person="sarah">
                <div class="body">
                    <div class="masthead">
                        <h2 class="name prettify"><a href="/sarah/list/comfort-watches/">Comfort Watches</a></h2>
                        <div class="content-reactions-strip -filmlist"><span class="value">18&nbsp;films</span></div>
                    </div>
                </div>
            </article>
        </div>
        """.trimIndent()

    @Test
    fun `getPopularLists parses lists with their owner usernames`() =
        runTest {
            val requestedUrls = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    requestedUrls += request.url.toString()
                    respond(popularListsHtml, HttpStatusCode.OK, headersOf("Content-Type", "text/html"))
                }
            val repository = createRepository(mockEngine)

            val result = repository.getPopularLists()

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(2, page.lists.size)
            assertEquals("Letterboxd's Top 500 Films", page.lists[0].name)
            assertEquals("letterboxds-top-500-films", page.lists[0].slug)
            assertEquals(500, page.lists[0].filmCount)
            assertEquals("official", page.lists[0].ownerUsername)
            assertEquals("Best of the Decade", page.lists[1].name)
            assertEquals("best-of-the-decade", page.lists[1].slug)
            assertEquals(42, page.lists[1].filmCount)
            assertEquals("dave", page.lists[1].ownerUsername)
            assertEquals(listOf("https://letterboxd.com/lists/popular/"), requestedUrls)
        }

    @Test
    fun `getPopularLists reports more pages when a next anchor exists`() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        popularListsHtml + userListsNextPageHtml,
                        HttpStatusCode.OK,
                        headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getPopularLists(page = 1)

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(2, page.lists.size)
            assertTrue(page.hasMore)
        }

    @Test
    fun `getPopularLists requests the paged url and reports no more pages on the last page`() =
        runTest {
            val requestedUrls = mutableListOf<String>()
            val mockEngine =
                MockEngine { request ->
                    requestedUrls += request.url.toString()
                    respond(
                        popularListSecondPageHtml + userListsLastPageHtml,
                        HttpStatusCode.OK,
                        headersOf("Content-Type", "text/html"),
                    )
                }
            val repository = createRepository(mockEngine)

            val result = repository.getPopularLists(page = 2)

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertEquals(1, page.lists.size)
            assertEquals("Comfort Watches", page.lists[0].name)
            assertEquals("comfort-watches", page.lists[0].slug)
            assertEquals(18, page.lists[0].filmCount)
            assertEquals("sarah", page.lists[0].ownerUsername)
            assertFalse(page.hasMore)
            assertEquals(listOf("https://letterboxd.com/lists/popular/page/2/"), requestedUrls)
        }

    @Test
    fun `getPopularLists returns an empty page when the listing is empty`() =
        runTest {
            val mockEngine =
                MockEngine { _ -> respond("", HttpStatusCode.OK, headersOf("Content-Type", "text/html")) }
            val repository = createRepository(mockEngine)

            val result = repository.getPopularLists()

            assertIs<ResultData.Success<*>>(result)
            val page = (result as ResultData.Success).data as UserListsPage
            assertTrue(page.lists.isEmpty())
            assertFalse(page.hasMore)
        }

    @Test
    fun `getPopularLists maps a not found page to NO_RESULTS`() =
        runTest {
            val mockEngine =
                MockEngine { _ -> respond("", HttpStatusCode.NotFound, headersOf("Content-Type", "text/html")) }
            val repository = createRepository(mockEngine)

            val result = repository.getPopularLists()

            assertIs<ResultData.Error<*>>(result)
            assertEquals(DataError.Remote.NO_RESULTS, (result as ResultData.Error).error)
        }
}
