package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode

data class RandomFilmState(
    val userName: String = "",
    val resultFilm: Film? = null,
    val resultError: DataError.Remote? = null,
    val isLoading: Boolean = false,
    val userNameSearchList: Set<String> = mutableSetOf(),
    val filmSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION,
)
