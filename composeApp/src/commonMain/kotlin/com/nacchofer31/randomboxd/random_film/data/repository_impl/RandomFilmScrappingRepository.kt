package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository

class RandomFilmScrappingRepository : RandomFilmRepository {
    override suspend fun getRandomMovie(userName: String): ResultData<Film, DataError.Remote> =
        try {
            val baseUrl = "https://letterboxd.com"
            val watchlistUrl = "$baseUrl/$userName/watchlist"

            val firstPage =
                Ksoup.parseGetRequest(watchlistUrl, httpRequestBuilder = {
                    headers.append("Access-Control-Allow-Origin", "*")
                })

            val allPages =
                firstPage
                    .select("a[href]")
                    .mapNotNull {
                        val href = it.attr("href")
                        if ("watchlist/page" in href) "$baseUrl$href" else null
                    }.toSet()
                    .plus(watchlistUrl)

            val allFilms = mutableListOf<Film>()

            for (pageUrl in allPages) {
                val doc =
                    Ksoup.parseGetRequest(pageUrl, httpRequestBuilder = {
                        headers.append("Access-Control-Allow-Origin", "*")
                    })
                val filmElements = doc.select("div.poster-container div.film-poster")

                filmElements.forEach { el ->
                    val name = el.attr("data-film-name")
                    val year = el.attr("data-film-release-year")
                    val slug = el.attr("data-film-slug")
                    val img = el.selectFirst("img")?.attr("src") ?: ""

                    val imageBigger = img.replace("-0-125-0-187-", "-0-230-0-345-")

                    allFilms.add(
                        Film(
                            slug = "$baseUrl$slug",
                            imageUrl = imageBigger,
                            releaseYear = if (year.isNotEmpty()) year.toInt() else null,
                            name = name,
                        ),
                    )
                }
            }

            if (allFilms.isEmpty()) {
                ResultData.Error(DataError.Remote.UNKNOWN)
            } else {
                ResultData.Success(allFilms.random())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultData.Error(DataError.Remote.SERVER)
        }
}
