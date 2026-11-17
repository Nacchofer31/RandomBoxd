package com.nacchofer31.randomboxd.tournament.domain.model

import kotlinx.serialization.Serializable

/**
 * Number of films competing in a tournament. Only powers of two are supported
 * so every round has an exact number of matches.
 */
@Serializable
enum class TournamentSize(
    val filmCount: Int,
) {
    TWO(2),
    FOUR(4),
    EIGHT(8),
    SIXTEEN(16),
    THIRTY_TWO(32),
    ;

    /** Number of elimination rounds until a champion is crowned. */
    val roundCount: Int
        get() = Integer.numberOfTrailingZeros(filmCount)

    /** Names of the rounds from the first (largest) to the last (Final). */
    val roundNames: List<TournamentRoundName>
        get() =
            when (this) {
                TWO -> {
                    listOf(TournamentRoundName.FINAL)
                }

                FOUR -> {
                    listOf(TournamentRoundName.SEMIFINALS, TournamentRoundName.FINAL)
                }

                EIGHT -> {
                    listOf(
                        TournamentRoundName.QUARTERFINALS,
                        TournamentRoundName.SEMIFINALS,
                        TournamentRoundName.FINAL,
                    )
                }

                SIXTEEN -> {
                    listOf(
                        TournamentRoundName.ROUND_OF_16,
                        TournamentRoundName.QUARTERFINALS,
                        TournamentRoundName.SEMIFINALS,
                        TournamentRoundName.FINAL,
                    )
                }

                THIRTY_TWO -> {
                    listOf(
                        TournamentRoundName.ROUND_OF_32,
                        TournamentRoundName.ROUND_OF_16,
                        TournamentRoundName.QUARTERFINALS,
                        TournamentRoundName.SEMIFINALS,
                        TournamentRoundName.FINAL,
                    )
                }
            }

    /** Total matches played across the whole tournament. */
    val totalMatchCount: Int
        get() = filmCount - 1

    companion object {
        /** Returns true when [filmCount] is a valid tournament size (a power of two). */
        fun isValidFilmCount(filmCount: Int): Boolean =
            filmCount >= TWO.filmCount &&
                filmCount <= THIRTY_TWO.filmCount &&
                (filmCount and (filmCount - 1)) == 0
    }
}

/** Canonical round names, mapped to localized strings in the UI layer. */
@Serializable
enum class TournamentRoundName {
    ROUND_OF_32,
    ROUND_OF_16,
    QUARTERFINALS,
    SEMIFINALS,
    FINAL,
}
