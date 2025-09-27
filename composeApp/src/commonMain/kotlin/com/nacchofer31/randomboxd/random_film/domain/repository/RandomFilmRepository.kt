package com.nacchofer31.randomboxd.random_film.domain.repository

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode

interface RandomFilmRepository {
    suspend fun getRandomMovie(userName: String): ResultData<Film, DataError.Remote>

    suspend fun getRandomMoviesFromSearchList(
        searchList: Set<String>,
        filmSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION,
    ): ResultData<Film, DataError.Remote>
}
