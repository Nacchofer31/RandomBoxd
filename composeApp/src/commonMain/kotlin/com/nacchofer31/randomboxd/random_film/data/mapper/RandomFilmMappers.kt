package com.nacchofer31.randomboxd.random_film.data.mapper

import com.nacchofer31.randomboxd.random_film.data.dto.FilmDto
import com.nacchofer31.randomboxd.random_film.domain.model.Film


fun FilmDto.toFilm(): Film {
    return Film(
        slug = slug,
        name = name,
        imageUrl = imageUrl,
        releaseYear = releaseYear.toInt()
    )
}
