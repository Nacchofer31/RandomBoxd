package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.nacchofer31.randomboxd.core.data.RandomBoxdEndpoints
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.data.dto.FilmDto
import com.nacchofer31.randomboxd.random_film.data.mapper.toFilm
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class RandomFilmRepositoryImpl(private val httpClient: HttpClient) : RandomFilmRepository {

    override suspend fun getRandomMovie(userName :String): ResultData<Film, DataError.Remote> {
        try {
            val filmResponse =
                httpClient.get(
                    urlString = RandomBoxdEndpoints.getUserRandomFilm(userName)
                )
                    .bodyAsText()

            val json = Json { ignoreUnknownKeys = true }

            val filmDto = json.decodeFromString<FilmDto>(filmResponse)

            return ResultData.Success(filmDto.toFilm())
        } catch (e: Exception) {
            return ResultData.Error(DataError.Remote.SERIALIZATION)
        }
    }
}