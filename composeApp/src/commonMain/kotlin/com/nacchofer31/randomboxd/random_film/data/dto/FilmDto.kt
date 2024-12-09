package com.nacchofer31.randomboxd.random_film.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilmDto (
    @SerialName("slug") val slug: String,
    @SerialName("image_url") val imageUrl : String,
    @SerialName("release_year") val releaseYear : String,
    @SerialName("film_name") val name : String,
)