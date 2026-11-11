package com.nacchofer31.randomboxd.random_film.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Film(
    val slug: String,
    val imageUrl: String,
    val releaseYear: Int? = null,
    val name: String,
)
