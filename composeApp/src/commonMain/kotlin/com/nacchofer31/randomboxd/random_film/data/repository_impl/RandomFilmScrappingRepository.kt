package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.fleeksoft.ksoup.Ksoup
import com.nacchofer31.randomboxd.core.data.RandomBoxdEndpoints
import com.nacchofer31.randomboxd.core.data.safeCall
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RandomFilmScrappingRepository(
    private val httpClient: HttpClient,
) : RandomFilmRepository {
    companion object {
        const val DATA_ITEM_SLUG = "data-item-slug"
        const val DATA_ITEM_NAME = "data-item-name"
        const val DATA_FILM_ID = "data-film-id"
        const val DATA_FILM_PAGE = "li.paginate-page"
        const val FILM_YEAR_REGEX = "\\((\\d{4})\\)"
        const val FILM_NAME_REGEX = "\\s*\\(\\d{4}\\)"
        const val FILM_POSTER_QUERY = "li.griditem > div.react-component[data-film-id]"
        const val FILM_POSTER_LIST_QUERY = "li.posteritem > div.react-component[data-film-id]"
        const val FILM_SCRIPT_QUERY = """script[type="application/ld+json"]"""
    }

    override suspend fun getRandomMovie(
        userName: String,
    ): ResultData<Film, DataError.Remote> =
        withContext(Dispatchers.IO) {
            val filmsResult = getFilmsFromUserWatchlist(userName)
            val films =
                when (filmsResult) {
                    is ResultData.Success -> filmsResult.data
                    is ResultData.Error -> return@withContext ResultData.Error(filmsResult.error)
                }

            if (films.isEmpty()) return@withContext ResultData.Error(DataError.Remote.NO_RESULTS)

            val chosenFilm = films.random()
            extractFilm(chosenFilm)
        }

    override suspend fun getRandomMoviesFromSearchList(
        searchList: Set<String>,
        filmSearchMode: FilmSearchMode,
    ): ResultData<Film, DataError.Remote> =
        withContext(Dispatchers.IO) {
            if (searchList.isEmpty()) return@withContext ResultData.Error(DataError.Remote.SERIALIZATION)

            val userFilmsResults = searchList.map { userName -> getFilmsFromUserWatchlist(userName) }
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

            val chosenFilm = combinedFilms.random()
            extractFilm(chosenFilm)
        }

    private suspend fun getFilmsFromUserWatchlist(userName: String): ResultData<List<Film>, DataError.Remote> {
        var baseUrl = ""
        var isWatchList = true
        if (userName.contains("/")) {
            isWatchList = false
            val textParts = userName.split("/")

            if (textParts.size == 2 && textParts[0].isNotBlank() && textParts[1].isNotBlank()) {
                val userName = textParts[0]
                val listName = textParts[1]

                baseUrl = RandomBoxdEndpoints.getUserNameFromList(userName, listName)
            }
        } else {
            baseUrl = RandomBoxdEndpoints.getUserNameWatchlist(userName)
        }

        val totalPages = getTotalPages(baseUrl)

        val films = mutableListOf<Film>()
        for (page in 1..totalPages) {
            val pageUrl = "$baseUrl/page/$page/"
            val htmlResult = getWebPage(pageUrl)
            val html =
                when (htmlResult) {
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
                val filmId = poster.attr(DATA_FILM_ID)
                val imageUrl = buildPosterUrl(filmId, slug)

                films.add(Film(slug, imageUrl, year, name))
            }
        }
        return ResultData.Success(films)
    }

    private suspend fun extractFilm(film: Film): ResultData<Film, DataError.Remote> {
        val posterResult = getPosterFromFilmPage(film.slug)
        val finalImageUrl =
            when (posterResult) {
                is ResultData.Success -> posterResult.data.ifEmpty { film.imageUrl }
                is ResultData.Error -> film.imageUrl
            }

        return ResultData.Success(
            film.copy(
                imageUrl = finalImageUrl,
                slug = RandomBoxdEndpoints.filmSlugUrl(film.slug),
            ),
        )
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
        val htmlResult = getWebPage(url)
        val html =
            when (htmlResult) {
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
        val htmlResult = getWebPage(url)
        val html =
            when (htmlResult) {
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
}
