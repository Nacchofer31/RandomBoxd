package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.fleeksoft.ksoup.Ksoup
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
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
import kotlin.random.Random

class RandomFilmScrappingRepository(
    private val httpClient: HttpClient,
) : RandomFilmRepository {
    override suspend fun getRandomMovie(
        userName: String,
    ): ResultData<Film, DataError.Remote> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = "https://letterboxd.com/$userName/watchlist"
                val totalPages = getTotalPages(baseUrl)

                val allFilms = mutableListOf<Film>()

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

                        allFilms.add(
                            Film(
                                slug = slug,
                                name = name,
                                releaseYear = year,
                                imageUrl = imageUrl,
                            ),
                        )
                    }
                }

                val chosenFilm = allFilms[Random.nextInt(allFilms.size)]

                val fixedImageUrl = getPosterFromFilmPage(chosenFilm.slug) ?: chosenFilm.imageUrl
                val finalFilm = chosenFilm.copy(imageUrl = fixedImageUrl, slug = "https://letterboxd.com/film/${chosenFilm.slug}/")

                ResultData.Success(finalFilm)
            } catch (e: Exception) {
                ResultData.Error(DataError.Remote.SERIALIZATION)
            }
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
        } catch (e: Exception) {
            1
        }

    private suspend fun getWebPage(url: String): String = httpClient.get(url).bodyAsText()
}
