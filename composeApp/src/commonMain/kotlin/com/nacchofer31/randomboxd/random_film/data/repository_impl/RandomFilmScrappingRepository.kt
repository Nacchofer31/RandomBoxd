package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.fleeksoft.ksoup.Ksoup
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
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
    override suspend fun getRandomMovie(
        userName: String,
    ): ResultData<Film, DataError.Remote> =
        withContext(Dispatchers.IO) {
            try {
                val films = getFilmsFromUserWatchlist(userName)
                if (films.isEmpty()) {
                    return@withContext ResultData.Error(DataError.Remote.SERIALIZATION)
                }

                val chosenFilm = films.random()
                val finalFilm = extractFilm(chosenFilm)

                ResultData.Success(finalFilm)
            } catch (e: Exception) {
                ResultData.Error(DataError.Remote.SERIALIZATION)
            }
        }

    override suspend fun getRandomMoviesFromSearchList(
        searchList: Set<String>,
        filmSearchMode: FilmSearchMode,
    ): ResultData<Film, DataError.Remote> =
        withContext(Dispatchers.IO) {
            try {
                if (searchList.isEmpty()) {
                    return@withContext ResultData.Error(DataError.Remote.SERIALIZATION)
                }

                val userFilms = searchList.map { userName -> getFilmsFromUserWatchlist(userName) }

                val combinedFilms: Set<Film> =
                    when (filmSearchMode) {
                        FilmSearchMode.INTERSECTION -> {
                            userFilms
                                .reduce { acc, films -> acc.intersect(films.toSet()).toMutableList() }
                                .toSet()
                        }

                        FilmSearchMode.UNION, null -> {
                            userFilms.flatten().toSet()
                        }
                    }

                if (combinedFilms.isEmpty()) {
                    return@withContext ResultData.Error(DataError.Remote.SERIALIZATION)
                }

                val chosenFilm = combinedFilms.random()
                val finalFilm = extractFilm(chosenFilm)

                ResultData.Success(finalFilm)
            } catch (e: Exception) {
                ResultData.Error(DataError.Remote.SERIALIZATION)
            }
        }

    private suspend fun getFilmsFromUserWatchlist(userName: String): List<Film> {
        val baseUrl = "https://letterboxd.com/$userName/watchlist"
        val totalPages = getTotalPages(baseUrl)

        val films = mutableListOf<Film>()
        for (page in 1..totalPages) {
            val pageUrl = "$baseUrl/page/$page/"
            val html = getWebPage(pageUrl)
            val doc = Ksoup.parse(html)

            val posters = doc.select("li.griditem > div.react-component[data-film-id]")
            posters.forEach { poster ->
                val slug = poster.attr("data-item-slug")
                val rawName = poster.attr("data-item-name")
                val year =
                    Regex("\\((\\d{4})\\)")
                        .find(rawName)
                        ?.groupValues
                        ?.get(1)
                        ?.toIntOrNull()
                val name = rawName.replace(Regex("\\s*\\(\\d{4}\\)"), "").trim()
                val filmId = poster.attr("data-film-id")
                val imageUrl = buildPosterUrl(filmId, slug)

                films.add(
                    Film(
                        slug = slug,
                        name = name,
                        releaseYear = year,
                        imageUrl = imageUrl,
                    ),
                )
            }
        }
        return films
    }

    private suspend fun extractFilm(film: Film): Film {
        val fixedImageUrl = getPosterFromFilmPage(film.slug) ?: film.imageUrl
        return film.copy(
            imageUrl = fixedImageUrl,
            slug = "https://letterboxd.com/film/${film.slug}/",
        )
    }

    private fun buildPosterUrl(
        filmId: String,
        slug: String,
    ): String {
        val segmentedId = filmId.toCharArray().joinToString("/") + "/"
        return "https://a.ltrbxd.com/resized/film-poster/$segmentedId$filmId-$slug-0-125-0-187-crop.jpg"
    }

    private suspend fun getPosterFromFilmPage(slug: String): String? {
        return try {
            val url = "https://letterboxd.com/film/$slug/"
            val html = getWebPage(url)
            val doc = Ksoup.parse(html)

            val script = doc.selectFirst("""script[type="application/ld+json"]""") ?: return null
            val rawJson =
                script
                    .data()
                    .substringAfter("*/", script.data())
                    .substringBefore("/*", script.data())
                    .trim()

            val jsonElement =
                Json.parseToJsonElement(rawJson)

            jsonElement.jsonObject["image"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getTotalPages(url: String): Int =
        try {
            val html = getWebPage(url)
            val doc = Ksoup.parse(html)
            val pages = doc.select("li.paginate-page").mapNotNull { it.text().toIntOrNull() }
            if (pages.isNotEmpty()) pages.maxOrNull() ?: 1 else 1
        } catch (_: Exception) {
            1
        }

    private suspend fun getWebPage(url: String): String = httpClient.get(url).bodyAsText()
}
