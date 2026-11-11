package com.randomboxd.feature.tournament.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.tournament.domain.model.Tournament
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import com.nacchofer31.randomboxd.tournament.presentation.screen.TournamentScreen
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentAction
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentPhase
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TournamentScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_phase_shows_setup_message_and_progress() {
        composeTestRule.setContent {
            TournamentScreen(
                state =
                    TournamentState(
                        phase = TournamentPhase.LOADING,
                        loadingProgress = 3,
                        loadingTotal = 8,
                    ),
                onAction = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithText("Setting up the bracket").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-tournament-progress").assertExists()
    }

    @Test
    fun match_phase_shows_films_versus_badge_and_round_progress() {
        val tournament = tournament(TournamentSize.EIGHT)
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.MATCH, tournament = tournament),
                onAction = {},
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithTag("test-match-film1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-match-film2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-match-vs-badge").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quarterfinals").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quarterfinals · Match 1 of 4").assertIsDisplayed()
    }

    @Test
    fun selecting_first_film_emits_action_with_match_index_and_film() {
        val tournament = tournament(TournamentSize.EIGHT)
        var captured: TournamentAction? = null
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.MATCH, tournament = tournament),
                onAction = { captured = it },
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithTag("test-match-film1").performClick()

        val action = captured
        assertTrue(action is TournamentAction.OnFilmSelected)
        action as TournamentAction.OnFilmSelected
        assertEquals(0, action.matchIndex)
        assertEquals("film-0", action.film.slug)
    }

    @Test
    fun round_summary_shows_winners_and_continue_emits_action() {
        var tournament = tournament(TournamentSize.EIGHT)
        tournament.currentRoundMatches.indices.forEach { index ->
            tournament = tournament.selectWinner(index, tournament.currentRoundMatches[index].film1)
        }
        var captured: TournamentAction? = null
        composeTestRule.setContent {
            TournamentScreen(
                state =
                    TournamentState(
                        phase = TournamentPhase.ROUND_SUMMARY,
                        tournament = tournament,
                    ),
                onAction = { captured = it },
                onBackClick = {},
            )
        }

        composeTestRule.onNodeWithTag("test-round-summary").assertIsDisplayed()
        tournament.currentRoundMatches.forEach { match ->
            composeTestRule.onNodeWithText(match.winner!!.name).assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag("test-continue-button").performClick()

        assertEquals(TournamentAction.OnNextRoundClicked, captured)
    }

    @Test
    fun champion_phase_shows_champion_stats_and_emits_button_callbacks() {
        val initialTournament = tournament(TournamentSize.TWO)
        val tournament = initialTournament.selectWinner(0, initialTournament.currentRoundMatches[0].film1)
        var captured: TournamentAction? = null
        var backCallbackCalled = false
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.CHAMPION, tournament = tournament),
                onAction = { captured = it },
                onBackClick = { backCallbackCalled = true },
            )
        }

        composeTestRule.onNodeWithText("Champion!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Film 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 films · 1 rounds · 1 matches").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-new-tournament").performClick()
        assertEquals(TournamentAction.OnNewTournamentClicked, captured)
        composeTestRule.onNodeWithTag("test-champion-back").performClick()
        assertTrue(backCallbackCalled)
    }

    @Test
    fun error_phase_shows_message_and_retry_emits_action() {
        var captured: TournamentAction? = null
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.ERROR),
                onAction = { captured = it },
                onBackClick = {},
            )
        }

        composeTestRule
            .onNodeWithText("Something went wrong loading the tournament")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-retry-button").performClick()

        assertEquals(TournamentAction.OnRetryClicked, captured)
    }

    private fun film(index: Int) =
        Film(
            slug = "film-$index",
            imageUrl = "https://example.com/$index.jpg",
            releaseYear = 2000 + index,
            name = "Film $index",
        )

    private fun tournament(size: TournamentSize): Tournament = Tournament.create(size, (0 until size.filmCount).map(::film))!!
}
