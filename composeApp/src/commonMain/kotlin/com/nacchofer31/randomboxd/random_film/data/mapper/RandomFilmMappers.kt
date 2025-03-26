package com.nacchofer31.randomboxd.random_film.data.mapper

import com.nacchofer31.randomboxd.random_film.data.dto.FilmDto
import com.nacchofer31.randomboxd.random_film.domain.model.Film


fun FilmDto.toFilm(): Film {
    val releaseYearValue = if (releaseYear.isEmpty()) null else releaseYear.toInt()
    return Film(
        slug = slug,
        name = name,
        imageUrl = imageUrl,
        releaseYear = releaseYearValue
    )
}
