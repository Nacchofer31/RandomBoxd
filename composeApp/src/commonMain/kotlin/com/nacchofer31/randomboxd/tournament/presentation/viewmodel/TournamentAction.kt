package com.nacchofer31.randomboxd.tournament.presentation.viewmodel

import com.nacchofer31.randomboxd.random_film.domain.model.Film

sealed interface TournamentAction {
    data class OnFilmSelected(
        val matchIndex: Int,
        val film: Film,
    ) : TournamentAction

    data object OnNextRoundClicked : TournamentAction

    data object OnNewTournamentClicked : TournamentAction

    data object OnRetryClicked : TournamentAction
}
