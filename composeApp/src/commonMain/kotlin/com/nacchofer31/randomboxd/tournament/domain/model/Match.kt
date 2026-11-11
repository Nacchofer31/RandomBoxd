package com.nacchofer31.randomboxd.tournament.domain.model

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import kotlinx.serialization.Serializable

/** A single versus matchup between two films. */
@Serializable
data class Match(
    val film1: Film,
    val film2: Film,
    val winner: Film? = null,
) {
    val isDecided: Boolean
        get() = winner != null
}
