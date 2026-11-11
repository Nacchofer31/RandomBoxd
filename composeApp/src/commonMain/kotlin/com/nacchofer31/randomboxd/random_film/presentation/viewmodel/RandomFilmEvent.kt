package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize

sealed interface RandomFilmEvent {
    data class TournamentStarted(
        val size: TournamentSize,
        val films: List<Film>,
        val userNames: Set<String>,
        val searchMode: FilmSearchMode,
        val genres: Set<FilmGenre>,
    ) : RandomFilmEvent
}
