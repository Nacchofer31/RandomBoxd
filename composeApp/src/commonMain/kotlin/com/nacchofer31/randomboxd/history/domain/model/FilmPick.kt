package com.nacchofer31.randomboxd.history.domain.model

import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class FilmPick
    constructor(
        val id: Int,
        val filmSlug: String,
        val filmName: String,
        val posterUrl: String,
        val releaseYear: Int?,
        val userNames: List<String>,
        val searchMode: FilmSearchMode,
        val selectedGenres: Set<FilmGenre>,
        val timestamp: Instant,
        val isFavorite: Boolean,
    )
