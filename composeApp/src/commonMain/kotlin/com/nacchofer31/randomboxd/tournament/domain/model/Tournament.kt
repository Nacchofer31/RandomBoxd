package com.nacchofer31.randomboxd.tournament.domain.model

import com.nacchofer31.randomboxd.random_film.domain.model.Film

/**
 * Immutable elimination bracket. Rounds are built lazily: [advanceRound] appends
 * the next round once every match of the current round is decided, keeping the
 * whole state in memory (ephemeral per session).
 */
data class Tournament(
    val size: TournamentSize,
    val rounds: List<List<Match>>,
    val currentRoundIndex: Int = 0,
) {
    /** Matches being played in the current round. */
    val currentRoundMatches: List<Match>
        get() = rounds[currentRoundIndex]

    val currentRoundName: TournamentRoundName
        get() = size.roundNames[currentRoundIndex]

    /** Matches of the current round that already have a winner. */
    val decidedMatchesInCurrentRound: Int
        get() = currentRoundMatches.count { it.isDecided }

    val isCurrentRoundComplete: Boolean
        get() = currentRoundMatches.all { it.isDecided }

    /** True when the final match has a winner. */
    val isFinished: Boolean
        get() = currentRoundMatches.size == 1 && isCurrentRoundComplete

    /** The crowned film, or null until the final is decided. */
    val champion: Film?
        get() = if (isFinished) rounds.last().single().winner else null

    /** Total matches decided across all played rounds. */
    val decidedMatchCount: Int
        get() = rounds.sumOf { round -> round.count { it.isDecided } }

    /**
     * Marks [winner] as the winner of the match at [matchIndex] in the current round.
     * The film must be one of the two competing in that match.
     */
    fun selectWinner(
        matchIndex: Int,
        winner: Film,
    ): Tournament {
        require(matchIndex in currentRoundMatches.indices) { "Match index $matchIndex out of bounds" }
        val match = currentRoundMatches[matchIndex]
        require(winner.slug == match.film1.slug || winner.slug == match.film2.slug) {
            "Selected film is not competing in match $matchIndex"
        }
        require(!isCurrentRoundComplete) { "Current round is already complete" }
        val decided =
            if (winner.slug == match.film1.slug) {
                match.copy(winner = match.film1)
            } else {
                match.copy(winner = match.film2)
            }
        val updatedRound = currentRoundMatches.toMutableList().also { it[matchIndex] = decided }
        return copy(rounds = rounds.toMutableList().also { it[currentRoundIndex] = updatedRound })
    }

    /**
     * Builds the next round from the current round winners. Requires every match
     * of the current round to be decided and the tournament not to be finished.
     */
    fun advanceRound(): Tournament {
        require(isCurrentRoundComplete) { "Current round has undecided matches" }
        require(!isFinished) { "Tournament is already finished" }
        val winners =
            currentRoundMatches.map { match ->
                checkNotNull(match.winner) { "Match has no winner despite round being complete" }
            }
        val nextRound = winners.chunked(2) { (film1, film2) -> Match(film1, film2) }
        return copy(
            rounds = rounds + listOf(nextRound),
            currentRoundIndex = currentRoundIndex + 1,
        )
    }

    companion object {
        /**
         * Builds the first-round bracket from exactly [TournamentSize.filmCount] films.
         * Films are matched in the given order (shuffle before calling to randomize).
         * Returns null when the input is invalid (wrong count or duplicated slugs).
         */
        fun create(
            size: TournamentSize,
            films: List<Film>,
        ): Tournament? {
            if (films.size != size.filmCount) return null
            if (films.distinctBy { it.slug }.size != films.size) return null
            val firstRound = films.chunked(2) { (film1, film2) -> Match(film1, film2) }
            return Tournament(size = size, rounds = listOf(firstRound))
        }
    }
}
