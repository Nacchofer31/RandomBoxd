package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.random_film.domain.model.Film

data class RandomFilmState (
    val userName: String? = null,
    val resultFilm: Film? = null,
    val isLoading: Boolean = false,
    )