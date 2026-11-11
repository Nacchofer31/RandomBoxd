package com.nacchofer31.randomboxd.tournament.domain.model

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TournamentTest {
    @Test
    fun `create accepts every supported size and builds the first round`() {
        TournamentSize.entries.forEach { size ->
            val tournament = Tournament.create(size, testFilms(size.filmCount))

            assertNotNull(tournament)
            assertEquals(size.filmCount / 2, tournament.currentRoundMatches.size)
            assertEquals(size.roundCount, size.roundNames.size)
            assertEquals(0, tournament.currentRoundIndex)
        }
    }

    @Test
    fun `create returns null for an invalid film count`() {
        val tournament = Tournament.create(TournamentSize.EIGHT, testFilms(3))

        assertNull(tournament)
    }

    @Test
    fun `create returns null when film slugs are duplicated`() {
        val films = testFilms(TournamentSize.FOUR.filmCount).toMutableList()
        films[1] = films[0].copy(name = "Duplicate")

        val tournament = Tournament.create(TournamentSize.FOUR, films)

        assertNull(tournament)
    }

    @Test
    fun `selectWinner marks matches and tracks round progress`() {
        val initial = requireNotNull(Tournament.create(TournamentSize.FOUR, testFilms(4)))
        val firstWinner = initial.currentRoundMatches[0].film1

        val afterFirstSelection = initial.selectWinner(0, firstWinner)

        assertTrue(afterFirstSelection.currentRoundMatches[0].isDecided)
        assertEquals(firstWinner, afterFirstSelection.currentRoundMatches[0].winner)
        assertEquals(1, afterFirstSelection.decidedMatchesInCurrentRound)
        assertFalse(afterFirstSelection.isCurrentRoundComplete)

        val secondWinner = afterFirstSelection.currentRoundMatches[1].film2
        val afterSecondSelection = afterFirstSelection.selectWinner(1, secondWinner)

        assertEquals(2, afterSecondSelection.decidedMatchesInCurrentRound)
        assertTrue(afterSecondSelection.isCurrentRoundComplete)
    }

    @Test
    fun `selectWinner rejects a film outside the match and an invalid index`() {
        val tournament = requireNotNull(Tournament.create(TournamentSize.FOUR, testFilms(4)))

        assertFailsWith<IllegalArgumentException> {
            tournament.selectWinner(0, testFilm(99))
        }
        assertFailsWith<IllegalArgumentException> {
            tournament.selectWinner(tournament.currentRoundMatches.size, tournament.currentRoundMatches[0].film1)
        }
        assertFailsWith<IllegalArgumentException> {
            tournament.selectWinner(-1, tournament.currentRoundMatches[0].film1)
        }
    }

    @Test
    fun `selectWinner rejects changes after the round is complete`() {
        var tournament = requireNotNull(Tournament.create(TournamentSize.FOUR, testFilms(4)))
        val matches = tournament.currentRoundMatches
        matches.forEachIndexed { index, match ->
            tournament = tournament.selectWinner(index, match.film1)
        }

        assertFailsWith<IllegalArgumentException> {
            tournament.selectWinner(0, matches[0].film2)
        }
    }

    @Test
    fun `advanceRound pairs winners in match order and increments the round index`() {
        var tournament = requireNotNull(Tournament.create(TournamentSize.FOUR, testFilms(4)))
        tournament = tournament.selectWinner(0, tournament.currentRoundMatches[0].film2)
        tournament = tournament.selectWinner(1, tournament.currentRoundMatches[1].film1)
        val expectedSlugs = tournament.currentRoundMatches.map { requireNotNull(it.winner).slug }

        val nextRound = tournament.advanceRound()

        assertEquals(1, nextRound.currentRoundIndex)
        assertEquals(1, nextRound.currentRoundMatches.size)
        assertEquals(
            expectedSlugs,
            nextRound.currentRoundMatches.single().let { listOf(it.film1.slug, it.film2.slug) },
        )
    }

    @Test
    fun `advanceRound requires a complete round`() {
        val tournament = requireNotNull(Tournament.create(TournamentSize.FOUR, testFilms(4)))

        assertFailsWith<IllegalArgumentException> {
            tournament.advanceRound()
        }
    }

    @Test
    fun `advanceRound rejects a finished tournament`() {
        val initial = requireNotNull(Tournament.create(TournamentSize.TWO, testFilms(2)))
        val finished = initial.selectWinner(0, initial.currentRoundMatches.single().film1)

        assertTrue(finished.isFinished)
        assertFailsWith<IllegalArgumentException> {
            finished.advanceRound()
        }
    }

    @Test
    fun `size eight play through crowns a champion and decides every match`() {
        var tournament = requireNotNull(Tournament.create(TournamentSize.EIGHT, testFilms(8)))

        while (!tournament.isFinished) {
            val matches = tournament.currentRoundMatches
            matches.forEachIndexed { index, match ->
                tournament = tournament.selectWinner(index, match.film1)
            }
            if (!tournament.isFinished) {
                tournament = tournament.advanceRound()
            }
        }

        assertTrue(tournament.isFinished)
        assertNotNull(tournament.champion)
        assertEquals("f0", tournament.champion?.slug)
        assertEquals(TournamentSize.EIGHT.totalMatchCount, tournament.decidedMatchCount)
        assertEquals(TournamentSize.EIGHT.roundCount, tournament.rounds.size)
    }

    @Test
    fun `isValidFilmCount accepts only supported powers of two`() {
        listOf(2, 4, 8, 16, 32).forEach { count ->
            assertTrue(TournamentSize.isValidFilmCount(count), "Expected $count to be valid")
        }
        listOf(0, 1, 3, 6, 12, 24, 64).forEach { count ->
            assertFalse(TournamentSize.isValidFilmCount(count), "Expected $count to be invalid")
        }
    }

    @Test
    fun `roundNames have the expected length and end with the final`() {
        val expectedLengths =
            mapOf(
                TournamentSize.TWO to 1,
                TournamentSize.FOUR to 2,
                TournamentSize.EIGHT to 3,
                TournamentSize.SIXTEEN to 4,
                TournamentSize.THIRTY_TWO to 5,
            )

        expectedLengths.forEach { (size, expectedLength) ->
            assertEquals(expectedLength, size.roundNames.size)
            assertEquals(TournamentRoundName.FINAL, size.roundNames.last())
        }
    }

    @Test
    fun `match is decided only when it has a winner`() {
        val undecided = Match(testFilm(0), testFilm(1))
        val decided = undecided.copy(winner = undecided.film2)

        assertFalse(undecided.isDecided)
        assertTrue(decided.isDecided)
    }

    private fun testFilms(count: Int): List<Film> = List(count, ::testFilm)

    private fun testFilm(index: Int): Film =
        Film(
            slug = "f$index",
            imageUrl = "https://example.com/f$index.jpg",
            releaseYear = 2000 + index,
            name = "Film $index",
        )
}
