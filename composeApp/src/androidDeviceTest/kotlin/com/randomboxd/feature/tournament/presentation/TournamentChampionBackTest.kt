package com.randomboxd.feature.tournament.presentation

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.asImage
import coil3.test.FakeImageLoaderEngine
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.tournament.domain.model.Tournament
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import com.nacchofer31.randomboxd.tournament.presentation.screen.TournamentScreen
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentAction
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentPhase
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Covers how the tournament hands its champion back to RandomFilm and, just as important, how it does not:
 * leaving before the final never delivers anything, and a brand new tournament cannot leak the previous champion.
 */
@RunWith(AndroidJUnit4::class)
class TournamentChampionBackTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val champion = film(0)
    private val runnerUp = film(1)

    @Before
    fun installFakeImageLoader() {
        setImageLoader()
    }

    @After
    @OptIn(DelicateCoilApi::class)
    fun resetImageLoader() {
        SingletonImageLoader.reset()
    }

    /** The champion screen renders a poster with Coil: keep the tests offline and the singleton class-local. */
    @OptIn(DelicateCoilApi::class)
    private fun setImageLoader() {
        // A previous test class may have already created the singleton, which makes setSafe throw.
        SingletonImageLoader.reset()
        SingletonImageLoader.setSafe {
            ImageLoader
                .Builder(context)
                .components {
                    add(
                        FakeImageLoaderEngine
                            .Builder()
                            .default(Bitmap.createBitmap(4, 6, Bitmap.Config.ARGB_8888).asImage())
                            .build(),
                    )
                }.build()
        }
    }

    @Test
    fun champion_back_button_delivers_the_real_champion_and_not_the_runner_up() {
        val finished = finishedTournament(champion, runnerUp)
        var delivered: Film? = null
        var backClicked = false
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.CHAMPION, tournament = finished),
                onAction = {},
                onBackClick = { backClicked = true },
                onChampionBack = { delivered = it },
            )
        }

        composeTestRule.onNodeWithTag("test-champion-back").performClick()

        assertEquals(champion, delivered)
        assertFalse(backClicked)
    }

    @Test
    fun header_back_in_champion_phase_delivers_the_same_champion() {
        val finished = finishedTournament(champion, runnerUp)
        var delivered: Film? = null
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.CHAMPION, tournament = finished),
                onAction = {},
                onBackClick = {},
                onChampionBack = { delivered = it },
            )
        }

        composeTestRule.onNodeWithTag("test-tournament-back-arrow").performClick()

        assertEquals(champion, delivered)
    }

    @Test
    fun system_back_in_champion_phase_delivers_the_same_champion() {
        val finished = finishedTournament(champion, runnerUp)
        var delivered: Film? = null
        var backClicked = false
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.CHAMPION, tournament = finished),
                onAction = {},
                onBackClick = { backClicked = true },
                onChampionBack = { delivered = it },
            )
        }

        composeTestRule.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()

        assertEquals(champion, delivered)
        assertFalse(backClicked)
    }

    @Test
    fun back_before_champion_uses_on_back_click_and_delivers_no_champion() {
        val bracket = tournamentOf(TournamentSize.TWO)
        var delivered: Film? = null
        var backClicked = false
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.MATCH, tournament = bracket),
                onAction = {},
                onBackClick = { backClicked = true },
                onChampionBack = { delivered = it },
            )
        }

        composeTestRule.onNodeWithTag("test-tournament-back-arrow").performClick()

        assertTrue(backClicked)
        assertNull(delivered)
    }

    @Test
    fun a_finished_bracket_outside_champion_phase_is_not_delivered() {
        val finished = finishedTournament(champion, runnerUp)
        var delivered: Film? = null
        var backClicked = false
        composeTestRule.setContent {
            // Defensive state: the bracket already has a champion but the phase has not advanced to CHAMPION yet.
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.ROUND_SUMMARY, tournament = finished),
                onAction = {},
                onBackClick = { backClicked = true },
                onChampionBack = { delivered = it },
            )
        }

        composeTestRule.onNodeWithTag("test-tournament-back-arrow").performClick()

        assertTrue(backClicked)
        assertNull(delivered)
    }

    @Test
    fun system_back_outside_champion_phase_is_not_intercepted() {
        val bracket = tournamentOf(TournamentSize.TWO)
        composeTestRule.setContent {
            TournamentScreen(
                state = TournamentState(phase = TournamentPhase.MATCH, tournament = bracket),
                onAction = {},
                onBackClick = {},
                onChampionBack = {},
            )
        }

        // The tournament only owns the back gesture while a champion is on screen; otherwise it stays untouched.
        assertFalse(composeTestRule.activity.onBackPressedDispatcher.hasEnabledCallbacks())
    }

    @Test
    fun starting_a_new_tournament_and_leaving_before_it_finishes_delivers_no_previous_champion() {
        val finished = finishedTournament(champion, runnerUp)
        var state by mutableStateOf(TournamentState(phase = TournamentPhase.CHAMPION, tournament = finished))
        var delivered: Film? = null
        var backClicked = false
        composeTestRule.setContent {
            TournamentScreen(
                state = state,
                onAction = { action ->
                    // What TournamentViewModel.startNewTournament() does: same films, brand new bracket, no champion.
                    if (action == TournamentAction.OnNewTournamentClicked) {
                        state =
                            TournamentState(
                                phase = TournamentPhase.MATCH,
                                tournament = tournamentOf(TournamentSize.TWO),
                            )
                    }
                },
                onBackClick = { backClicked = true },
                onChampionBack = { delivered = it },
            )
        }

        composeTestRule.onNodeWithTag("test-new-tournament").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("test-tournament-back-arrow").performClick()

        assertTrue(backClicked)
        assertNull(delivered)
        assertNull(state.tournament?.champion)
    }

    private fun finishedTournament(
        winner: Film,
        loser: Film,
    ): Tournament {
        val bracket = Tournament.create(TournamentSize.TWO, listOf(winner, loser))!!
        return bracket.selectWinner(0, winner)
    }

    private fun tournamentOf(size: TournamentSize): Tournament = Tournament.create(size, (0 until size.filmCount).map(::film))!!

    private fun film(index: Int) =
        Film(
            slug = "film-$index",
            imageUrl = "https://example.com/$index.jpg",
            releaseYear = 2000 + index,
            name = "Film $index",
        )
}
