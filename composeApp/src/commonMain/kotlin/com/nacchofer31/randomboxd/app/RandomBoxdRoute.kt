package com.nacchofer31.randomboxd.app

import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import kotlinx.serialization.Serializable

sealed interface RandomBoxdRoute {
    @Serializable
    data object Onboarding : RandomBoxdRoute

    @Serializable
    data object Home : RandomBoxdRoute

    @Serializable
    data object RandomFilm : RandomBoxdRoute

    @Serializable
    data object History : RandomBoxdRoute

    @Serializable
    data class Tournament(
        val size: TournamentSize,
    ) : RandomBoxdRoute
}
