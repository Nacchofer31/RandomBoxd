package com.nacchofer31.randomboxd.tournament.presentation

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Temporary holder for tournament films and search context between navigation events.
 * Since Compose Navigation type-safe routes cannot serialize List<Film>,
 * we use this simple holder to pass films from RandomFilmScreen to TournamentScreen.
 *
 * It also carries the tournament champion back to RandomFilm: the tournament publishes it and the
 * RandomFilm destination consumes it, so it is applied exactly once (surviving recompositions and
 * configuration changes because it lives in a Koin singleton, not in a saveable route argument).
 */
class TournamentFilmProvider {
    var pendingFilms: List<Film> = emptyList()
    var pendingUserNames: Set<String> = emptySet()
    var pendingSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION
    var pendingGenres: Set<FilmGenre> = emptySet()

    private val mutableChampion = MutableStateFlow<Film?>(null)

    /** Latest champion waiting to be shown as the RandomFilm result, `null` when there is none. */
    val champion: StateFlow<Film?> = mutableChampion.asStateFlow()

    fun publishChampion(film: Film) {
        mutableChampion.value = film
    }

    /** Returns the pending champion, if any, and clears it so it can only be applied once. */
    fun consumeChampion(): Film? {
        val pendingChampion = mutableChampion.value ?: return null
        mutableChampion.value = null
        return pendingChampion
    }
}
