package com.nacchofer31.randomboxd.tournament.presentation.viewmodel

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.tournament.domain.model.Tournament

enum class TournamentPhase { LOADING, MATCH, ROUND_SUMMARY, CHAMPION, ERROR }

data class TournamentState(
    val phase: TournamentPhase = TournamentPhase.LOADING,
    val tournament: Tournament? = null,
    val loadingProgress: Int = 0,
    val loadingTotal: Int = 0,
    val error: DataError.Remote? = null,
)
