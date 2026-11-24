package com.nacchofer31.randomboxd.random_film.domain.repository

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserListsPage

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

    suspend fun getUserLists(
        userName: String,
        page: Int = 1,
    ): ResultData<UserListsPage, DataError.Remote>

    /** Site wide listing of the most popular lists, scraped from `letterboxd.com/lists/popular/`. */
    suspend fun getPopularLists(page: Int = 1): ResultData<UserListsPage, DataError.Remote>
}
