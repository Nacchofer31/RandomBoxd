package com.nacchofer31.randomboxd.random_film.domain.repository

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode

interface RandomFilmRepository {
    suspend fun getRandomMovies(
        userName: String,
        selectedGenres: Set<FilmGenre> = emptySet(),
    ): ResultData<Set<Film>, DataError.Remote>

    suspend fun getRandomMoviesFromSearchList(
        searchList: Set<String>,
        filmSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION,
        selectedGenres: Set<FilmGenre> = emptySet(),
    ): ResultData<Set<Film>, DataError.Remote>

    suspend fun extractResultMovie(
        film: Film,
    ): ResultData<Film, DataError.Remote>
}
