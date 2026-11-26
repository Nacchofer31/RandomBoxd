package com.randomboxd.feature.random_film.presentation

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.presentation.components.RandomFilmInfoView
import org.jetbrains.compose.resources.stringResource
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_new_badge
import randomboxd.composeapp.generated.resources.tip_genre_filter
import randomboxd.composeapp.generated.resources.tip_tournament
import randomboxd.composeapp.generated.resources.tip_username_format

@RunWith(AndroidJUnit4::class)
class RandomFilmInfoViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private var texts: InfoViewTexts? = null

    private data class InfoViewTexts(
        val newBadge: String,
        val tournamentTip: String,
        val genreTip: String,
        val usernameTip: String,
    )

    private fun setInfoViewContent() {
        composeTestRule.setContent {
            // Resolve the expected labels from the resources so the assertions do not
            // depend on the device locale or on hardcoded English literals.
            val resolved =
                InfoViewTexts(
                    newBadge = stringResource(Res.string.onboarding_new_badge),
                    tournamentTip = stringResource(Res.string.tip_tournament),
                    genreTip = stringResource(Res.string.tip_genre_filter),
                    usernameTip = stringResource(Res.string.tip_username_format),
                )
            SideEffect { texts = resolved }
            RandomFilmInfoView()
        }
        composeTestRule.waitForIdle()
    }

    private fun requireTexts(): InfoViewTexts = requireNotNull(texts) { "Info view labels were not resolved" }

    @Test
    fun info_view_shows_ready_to_spin_text() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithText("Ready to spin?").assertIsDisplayed()
    }

    @Test
    fun info_view_shows_roulette_icon() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithContentDescription("Roulette").assertIsDisplayed()
    }

    @Test
    fun info_view_shows_tip_username_format() {
        setInfoViewContent()

        composeTestRule.onNodeWithText(requireTexts().usernameTip).assertIsDisplayed()
    }

    @Test
    fun info_view_shows_tip_hold_submit() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithText("Long-press Submit or tags for multi-user mode").assertIsDisplayed()
    }

    @Test
    fun info_view_shows_tip_genre_filter() {
        setInfoViewContent()

        composeTestRule.onNodeWithText(requireTexts().genreTip).assertIsDisplayed()
    }

    @Test
    fun info_view_announces_tournament_novelty() {
        setInfoViewContent()

        composeTestRule.onNodeWithText(requireTexts().tournamentTip).assertIsDisplayed()
        composeTestRule.onNodeWithText(requireTexts().newBadge).assertIsDisplayed()
    }

    @Test
    fun info_view_only_shows_one_novelty_card() {
        setInfoViewContent()

        // The novelty slot belongs to Tournament now: a single NEW badge is rendered.
        composeTestRule.onAllNodesWithText(requireTexts().newBadge).assertCountEquals(1)
    }

    @Test
    fun info_view_no_longer_announces_history() {
        setInfoViewContent()

        // The History tip string was removed from the resources together with its card, so the
        // legacy copy is asserted by its old English text to make sure it never comes back.
        composeTestRule
            .onNodeWithText("Now you can view your pick history by pressing the History button")
            .assertDoesNotExist()
    }
}
