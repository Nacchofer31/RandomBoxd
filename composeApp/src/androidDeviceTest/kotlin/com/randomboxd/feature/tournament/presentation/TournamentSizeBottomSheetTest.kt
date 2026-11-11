package com.randomboxd.feature.tournament.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import com.nacchofer31.randomboxd.tournament.presentation.components.TournamentSizeBottomSheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TournamentSizeBottomSheetTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tournament_size_bottom_sheet_shows_all_size_chips() {
        composeTestRule.setContent {
            TournamentSizeBottomSheet(
                selectedSize = null,
                availableFilms = 32,
                onStart = {},
                onDismiss = {},
            )
        }

        listOf(2, 4, 8, 16, 32).forEach { filmCount ->
            composeTestRule.onNodeWithTag("test-tournament-size-$filmCount").assertIsDisplayed()
        }
    }

    @Test
    fun tournament_size_bottom_sheet_shows_title() {
        composeTestRule.setContent {
            TournamentSizeBottomSheet(
                selectedSize = null,
                availableFilms = 32,
                onStart = {},
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Tournament Size").assertIsDisplayed()
    }

    @Test
    fun clicking_size_chip_then_start_calls_onStart_with_that_size() {
        var started: TournamentSize? = null
        composeTestRule.setContent {
            TournamentSizeBottomSheet(
                selectedSize = null,
                availableFilms = 32,
                onStart = { started = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithTag("test-tournament-size-16").performClick()
        composeTestRule.onNodeWithTag("test-tournament-start").performClick()

        assertEquals(TournamentSize.SIXTEEN, started)
    }

    @Test
    fun insufficient_films_disables_start_and_shows_warning() {
        var started: TournamentSize? = null
        composeTestRule.setContent {
            TournamentSizeBottomSheet(
                selectedSize = null,
                availableFilms = 3,
                onStart = { started = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithTag("test-tournament-size-8").performClick()
        composeTestRule
            .onNodeWithTag("test-tournament-start")
            .assertIsNotEnabled()
            .performClick()

        assertNull(started)
        composeTestRule.onNodeWithTag("test-tournament-not-enough-films").assertIsDisplayed()
    }

    @Test
    fun default_eight_selection_shows_eight_film_info() {
        composeTestRule.setContent {
            TournamentSizeBottomSheet(
                selectedSize = null,
                availableFilms = 32,
                onStart = {},
                onDismiss = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                "8 films selected · Quarterfinals (4 matches) → Semifinals (2) → Final (1)",
                substring = true,
            ).assertIsDisplayed()
    }
}
