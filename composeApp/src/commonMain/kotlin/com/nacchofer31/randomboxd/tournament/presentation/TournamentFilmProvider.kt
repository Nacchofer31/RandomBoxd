package com.nacchofer31.randomboxd.tournament.presentation

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode

/**
 * Temporary holder for tournament films and search context between navigation events.
 * Since Compose Navigation type-safe routes cannot serialize List<Film>,
 * we use this simple holder to pass films from RandomFilmScreen to TournamentScreen.
 */
class TournamentFilmProvider {
    var pendingFilms: List<Film> = emptyList()
    var pendingUserNames: Set<String> = emptySet()
    var pendingSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION
    var pendingGenres: Set<FilmGenre> = emptySet()
}
