package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.fleeksoft.ksoup.Ksoup
import com.nacchofer31.randomboxd.core.data.RandomBoxdEndpoints
import com.nacchofer31.randomboxd.core.data.responseToResult
import com.nacchofer31.randomboxd.core.data.safeCall
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.random_film.domain.model.UserListsPage
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.coroutineContext

class RandomFilmScrappingRepository(
    private val httpClient: HttpClient,
) : RandomFilmRepository {
    companion object {
        const val DATA_ITEM_SLUG = "data-item-slug"
        const val DATA_ITEM_NAME = "data-item-name"
        const val DATA_POSTERED_IDENTIFIER = "data-postered-identifier"
        const val DATA_FILM_PAGE = "li.paginate-page"
        const val FILM_YEAR_REGEX = "\\((\\d{4})\\)"
        const val FILM_NAME_REGEX = "\\s*\\(\\d{4}\\)"
        const val FILM_POSTER_QUERY = "li.griditem > div.react-component[data-item-slug]"
        const val FILM_POSTER_LIST_QUERY = "li.posteritem > div.react-component[data-item-slug]"
        const val FILM_SCRIPT_QUERY = """script[type="application/ld+json"]"""
        const val USER_LISTS_ITEM_QUERY = "div.listitem"
        const val USER_LIST_NAME_QUERY = "h2.name a"
        const val USER_LIST_FILM_COUNT_QUERY = "span.value"
        const val USER_LISTS_NEXT_PAGE_QUERY = "div.paginate-nextprev a.next"
        const val USER_LIST_PATH_MARKER = "/list/"
        const val LIST_FILM_COUNT_REGEX = "\\d+"
    }

    override suspend fun getRandomMovies(
        userName: String,
        selectedGenres: Set<FilmGenre>,
    ): ResultData<Set<Film>, DataError.Remote> =
        withContext(Dispatchers.IO) {
            val films =
                when (val filmsResult = getFilmsFromUserWatchlist(userName, selectedGenres)) {
                    is ResultData.Success -> filmsResult.data
                    is ResultData.Error -> return@withContext ResultData.Error(filmsResult.error)
                }

            if (films.isEmpty()) return@withContext ResultData.Error(DataError.Remote.NO_RESULTS)

            return@withContext ResultData.Success(films.toSet())
        }

    override suspend fun getRandomMoviesFromSearchList(
        searchList: Set<String>,
        filmSearchMode: FilmSearchMode,
        selectedGenres: Set<FilmGenre>,
    ): ResultData<Set<Film>, DataError.Remote> =
        withContext(Dispatchers.IO) {
            if (searchList.isEmpty()) return@withContext ResultData.Error(DataError.Remote.SERIALIZATION)

            val userFilmsResults = searchList.map { userName -> getFilmsFromUserWatchlist(userName, selectedGenres) }
            val userFilms: List<List<Film>> = mutableListOf()

            for (result in userFilmsResults) {
                when (result) {
                    is ResultData.Success -> (userFilms as MutableList).add(result.data)
                    is ResultData.Error -> return@withContext ResultData.Error(result.error)
                }
            }

            val combinedFilms: Set<Film> =
                when (filmSearchMode) {
                    FilmSearchMode.INTERSECTION -> {
                        if (userFilms.isEmpty()) {
                            emptySet()
                        } else {
                            userFilms.reduce { acc, films -> acc.intersect(films.toSet()).toMutableList() }.toSet()
                        }
                    }

                    FilmSearchMode.UNION -> {
                        userFilms.flatten().toSet()
                    }
                }

            if (combinedFilms.isEmpty()) return@withContext ResultData.Error(DataError.Remote.NO_RESULTS)

            return@withContext ResultData.Success(combinedFilms)
        }

    override suspend fun extractResultMovie(film: Film): ResultData<Film, DataError.Remote> = extractFilm(film)

    override suspend fun getUserLists(
        userName: String,
        page: Int,
    ): ResultData<UserListsPage, DataError.Remote> =
        withContext(Dispatchers.IO) {
            // Pages are scrapped on demand (one page per scroll-to-end) to stay Cloudflare friendly.
            val html =
                when (val htmlResult = getWebPageOrNotFound(RandomBoxdEndpoints.getUserLists(userName, page))) {
                    is ResultData.Success -> htmlResult.data
                    is ResultData.Error -> return@withContext ResultData.Error(htmlResult.error)
                }

            parseUserListsPage(html)
        }

    private fun parseUserListsPage(html: String): ResultData<UserListsPage, DataError.Remote> {
        val doc =
            try {
                Ksoup.parse(html)
            } catch (_: Exception) {
                return ResultData.Error(DataError.Remote.SERIALIZATION)
            }

        val items = doc.select(USER_LISTS_ITEM_QUERY)
        // A real `a.next` anchor means there is another page; the last page renders a disabled span instead.
        val hasMore = doc.selectFirst(USER_LISTS_NEXT_PAGE_QUERY) != null

        // Users with no lists render an empty-state section, and out-of-range pages return an empty body.
        if (items.isEmpty()) return ResultData.Success(UserListsPage(lists = emptyList(), hasMore = false))

        val userLists =
            items
                .mapNotNull { item ->
                    val name =
                        item
                            .selectFirst(USER_LIST_NAME_QUERY)
                            ?.text()
                            ?.trim()
                            .orEmpty()
                    if (name.isEmpty()) return@mapNotNull null

                    val slug = parseUserListSlug(item.selectFirst(USER_LIST_NAME_QUERY)?.attr("href").orEmpty())
                    if (slug == null) return@mapNotNull null

                    val filmCount =
                        item
                            .selectFirst(USER_LIST_FILM_COUNT_QUERY)
                            ?.text()
                            ?.let { Regex(LIST_FILM_COUNT_REGEX).find(it)?.value?.toIntOrNull() }

                    UserList(name = name, slug = slug, filmCount = filmCount)
                }.distinctBy { it.slug }

        if (userLists.isEmpty()) return ResultData.Error(DataError.Remote.SERIALIZATION)

        return ResultData.Success(UserListsPage(lists = userLists, hasMore = hasMore))
    }

    private fun parseUserListSlug(href: String): String? {
        val path = href.substringBefore('?').substringBefore('#')
        val markerIndex = path.indexOf(USER_LIST_PATH_MARKER)
        if (markerIndex < 0) return null

        return path
            .substring(markerIndex + USER_LIST_PATH_MARKER.length)
            .trim('/')
            .ifBlank { null }
    }

    private suspend fun getFilmsFromUserWatchlist(
        userName: String,
        selectedGenres: Set<FilmGenre> = emptySet(),
    ): ResultData<List<Film>, DataError.Remote> {
        val genreSlugs = selectedGenres.joinToString("+") { it.slug }
        var baseUrl = ""
        var isWatchList = true
        if (userName.contains("/")) {
            isWatchList = false
            val textParts = userName.split("/")

            if (textParts.size == 2 && textParts[0].isNotBlank() && textParts[1].isNotBlank()) {
                val userName = textParts[0]
                val listName = textParts[1]

                baseUrl = RandomBoxdEndpoints.getUserNameFromList(userName, listName, genreSlugs)
            }
        } else {
            baseUrl = RandomBoxdEndpoints.getUserNameWatchlist(userName, genreSlugs)
        }

        val totalPages = getTotalPages(baseUrl)

        val films = mutableListOf<Film>()
        for (page in 1..totalPages) {
            val pageUrl = "$baseUrl/page/$page/"
            val html =
                when (val htmlResult = getWebPage(pageUrl)) {
                    is ResultData.Success -> htmlResult.data
                    is ResultData.Error -> return ResultData.Error(htmlResult.error)
                }

            val doc = Ksoup.parse(html)
            val posters = doc.select(if (isWatchList) FILM_POSTER_QUERY else FILM_POSTER_LIST_QUERY)

            posters.forEach { poster ->
                val slug = poster.attr(DATA_ITEM_SLUG)
                val rawName = poster.attr(DATA_ITEM_NAME)
                val year =
                    Regex(FILM_YEAR_REGEX)
                        .find(rawName)
                        ?.groupValues
                        ?.get(1)
                        ?.toIntOrNull()
                val name = rawName.replace(Regex(FILM_NAME_REGEX), "").trim()
                val filmId =
                    try {
                        val identifierJson = Json.parseToJsonElement(poster.attr(DATA_POSTERED_IDENTIFIER))
                        identifierJson.jsonObject["uid"]
                            ?.jsonPrimitive
                            ?.content
                            ?.removePrefix("film:") ?: ""
                    } catch (_: Exception) {
                        ""
                    }
                val imageUrl = buildPosterUrl(filmId, slug)

                films.add(Film(slug, imageUrl, year, name))
            }
        }
        return ResultData.Success(films)
    }

    private suspend fun extractFilm(film: Film): ResultData<Film, DataError.Remote> =
        try {
            val finalImageUrl =
                when (val posterResult = getPosterFromFilmPage(film.slug)) {
                    is ResultData.Success -> posterResult.data.ifEmpty { film.imageUrl }
                    is ResultData.Error -> film.imageUrl
                }

            ResultData.Success(
                film.copy(
                    imageUrl = finalImageUrl,
                    slug = RandomBoxdEndpoints.filmSlugUrl(film.slug),
                ),
            )
        } catch (_: Exception) {
            ResultData.Error(DataError.Remote.SERIALIZATION)
        }

    private fun buildPosterUrl(
        filmId: String,
        slug: String,
    ): String {
        val segmentedId = filmId.toCharArray().joinToString("/") + "/"
        return RandomBoxdEndpoints.filmPosterUrl(segmentedId, filmId, slug)
    }

    private suspend fun getPosterFromFilmPage(slug: String): ResultData<String, DataError.Remote> {
        val url = RandomBoxdEndpoints.filmSlugUrl(slug)
        val html =
            when (val htmlResult = getWebPage(url)) {
                is ResultData.Success -> htmlResult.data
                is ResultData.Error -> return ResultData.Error(htmlResult.error)
            }

        return try {
            val doc = Ksoup.parse(html)
            val script = doc.selectFirst(FILM_SCRIPT_QUERY) ?: return ResultData.Success("")
            val rawJson =
                script
                    .data()
                    .substringAfter("*/", script.data())
                    .substringBefore("/*", script.data())
                    .trim()
            val jsonElement = Json.parseToJsonElement(rawJson)
            val imageUrl = jsonElement.jsonObject["image"]?.jsonPrimitive?.content ?: ""
            ResultData.Success(imageUrl)
        } catch (_: Exception) {
            ResultData.Error(DataError.Remote.SERIALIZATION)
        }
    }

    private suspend fun getTotalPages(url: String): Int {
        val html =
            when (val htmlResult = getWebPage(url)) {
                is ResultData.Success -> htmlResult.data
                is ResultData.Error -> return 1
            }

        return try {
            val doc = Ksoup.parse(html)
            val pages = doc.select(DATA_FILM_PAGE).mapNotNull { it.text().toIntOrNull() }
            pages.maxOrNull() ?: 1
        } catch (_: Exception) {
            1
        }
    }

    private suspend fun getWebPage(url: String): ResultData<String, DataError.Remote> =
        safeCall<HttpResponse> { httpClient.get(url) }.let { result ->
            when (result) {
                is ResultData.Success -> ResultData.Success(result.data.bodyAsText())
                is ResultData.Error -> ResultData.Error(result.error)
            }
        }

    /**
     * Same behaviour as [getWebPage] but maps HTTP 404 to [DataError.Remote.NO_RESULTS], so callers can tell a
     * nonexistent user apart from a generic failure.
     */
    private suspend fun getWebPageOrNotFound(url: String): ResultData<String, DataError.Remote> {
        val response =
            try {
                httpClient.get(url)
            } catch (_: SocketTimeoutException) {
                return ResultData.Error(DataError.Remote.REQUEST_TIMEOUT)
            } catch (_: UnresolvedAddressException) {
                return ResultData.Error(DataError.Remote.NO_INTERNET)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                return ResultData.Error(DataError.Remote.NO_INTERNET)
            }

        if (response.status == HttpStatusCode.NotFound) return ResultData.Error(DataError.Remote.NO_RESULTS)

        return when (val result = responseToResult<HttpResponse>(response)) {
            is ResultData.Success -> ResultData.Success(result.data.bodyAsText())
            is ResultData.Error -> ResultData.Error(result.error)
        }
    }
}
