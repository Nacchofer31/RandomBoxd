package com.randomboxd.onboarding.presentation

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.onboarding.presentation.OnboardingScreen
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_genre_title
import randomboxd.composeapp.generated.resources.onboarding_get_started
import randomboxd.composeapp.generated.resources.onboarding_history_title
import randomboxd.composeapp.generated.resources.onboarding_multiuser_title
import randomboxd.composeapp.generated.resources.onboarding_new_badge
import randomboxd.composeapp.generated.resources.onboarding_next
import randomboxd.composeapp.generated.resources.onboarding_random_title
import randomboxd.composeapp.generated.resources.onboarding_search_feature3
import randomboxd.composeapp.generated.resources.onboarding_search_title
import randomboxd.composeapp.generated.resources.onboarding_skip
import randomboxd.composeapp.generated.resources.onboarding_start_exploring
import randomboxd.composeapp.generated.resources.onboarding_tournament_feature3
import randomboxd.composeapp.generated.resources.onboarding_tournament_subtitle
import randomboxd.composeapp.generated.resources.onboarding_tournament_title
import randomboxd.composeapp.generated.resources.onboarding_welcome_title

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private var texts: OnboardingTexts? = null

    private data class OnboardingTexts(
        val welcomeTitle: String,
        val randomTitle: String,
        val searchTitle: String,
        val searchFeature3: String,
        val multiUserTitle: String,
        val genreTitle: String,
        val historyTitle: String,
        val tournamentTitle: String,
        val tournamentSubtitle: String,
        val tournamentFeature3: String,
        val newBadge: String,
        val getStarted: String,
        val next: String,
        val skip: String,
        val startExploring: String,
    )

    private fun setOnboardingContent(onFinish: () -> Unit = {}) {
        composeTestRule.setContent {
            // Resolve the expected labels from the resources so the assertions do not
            // depend on the device locale or on hardcoded English literals.
            val resolved =
                OnboardingTexts(
                    welcomeTitle = stringResource(Res.string.onboarding_welcome_title),
                    randomTitle = stringResource(Res.string.onboarding_random_title),
                    searchTitle = stringResource(Res.string.onboarding_search_title),
                    searchFeature3 = stringResource(Res.string.onboarding_search_feature3),
                    multiUserTitle = stringResource(Res.string.onboarding_multiuser_title),
                    genreTitle = stringResource(Res.string.onboarding_genre_title),
                    historyTitle = stringResource(Res.string.onboarding_history_title),
                    tournamentTitle = stringResource(Res.string.onboarding_tournament_title),
                    tournamentSubtitle = stringResource(Res.string.onboarding_tournament_subtitle),
                    tournamentFeature3 = stringResource(Res.string.onboarding_tournament_feature3),
                    newBadge = stringResource(Res.string.onboarding_new_badge),
                    getStarted = stringResource(Res.string.onboarding_get_started),
                    next = stringResource(Res.string.onboarding_next),
                    skip = stringResource(Res.string.onboarding_skip),
                    startExploring = stringResource(Res.string.onboarding_start_exploring),
                )
            SideEffect { texts = resolved }
            OnboardingScreen(onFinish = onFinish)
        }
        composeTestRule.waitForIdle()
    }

    private fun requireTexts(): OnboardingTexts = requireNotNull(texts) { "Onboarding labels were not resolved" }

    /**
     * The pager keeps the neighbouring pages composed, so a badge can exist in the tree while
     * being off-screen. Counting only the displayed nodes tells us what the user actually sees.
     */
    private fun countDisplayedNodesWithText(text: String): Int {
        val nodes = composeTestRule.onAllNodesWithText(text)
        val nodeCount = nodes.fetchSemanticsNodes().size
        return (0 until nodeCount).count { nodes[it].isDisplayed() }
    }

    private fun clickNext() {
        composeTestRule.onNodeWithText(requireTexts().next).performClick()
        composeTestRule.waitForIdle()
    }

    private fun goToSearchPage() {
        composeTestRule.onNodeWithText(requireTexts().getStarted).performClick()
        composeTestRule.waitForIdle()
        // Page 2 (Random Selection) -> page 3 (Search & Tags)
        clickNext()
    }

    @Test
    fun onboarding_screen_shows_first_page_on_launch() {
        setOnboardingContent()
        val labels = requireTexts()

        composeTestRule.onNodeWithText(labels.welcomeTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.getStarted).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.skip).assertIsDisplayed()
    }

    @Test
    fun onboarding_skip_button_calls_on_finish() {
        var finished = false
        setOnboardingContent(onFinish = { finished = true })

        composeTestRule.onNodeWithText(requireTexts().skip).performClick()

        assertTrue(finished)
    }

    @Test
    fun onboarding_next_button_advances_to_second_page() {
        setOnboardingContent()
        val labels = requireTexts()

        composeTestRule.onNodeWithText(labels.getStarted).performClick()

        composeTestRule.onNodeWithText(labels.randomTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.next).assertIsDisplayed()
    }

    @Test
    fun onboarding_navigates_through_all_seven_pages_in_order_and_finishes_on_tournament() {
        var finished = false
        setOnboardingContent(onFinish = { finished = true })
        val labels = requireTexts()

        composeTestRule.onNodeWithText(labels.welcomeTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.getStarted).performClick()
        composeTestRule.waitForIdle()

        // Pages 2 to 6, each advanced with the Next button.
        val middlePages =
            listOf(
                labels.randomTitle,
                labels.searchTitle,
                labels.multiUserTitle,
                labels.genreTitle,
                labels.historyTitle,
            )
        middlePages.forEach { title ->
            composeTestRule.onNodeWithText(title).assertIsDisplayed()
            clickNext()
        }

        // Page 7 is the Tournament page and the only one showing "Start Exploring".
        composeTestRule.onNodeWithText(labels.tournamentTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.startExploring).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.startExploring).performClick()

        assertTrue(finished)
    }

    @Test
    fun onboarding_search_page_shows_new_badge_and_discover_lists_feature() {
        setOnboardingContent()
        val labels = requireTexts()

        goToSearchPage()

        composeTestRule.onNodeWithText(labels.searchTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.newBadge).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.searchFeature3).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun onboarding_history_page_has_no_new_badge_and_tournament_page_has_it() {
        setOnboardingContent()
        val labels = requireTexts()

        composeTestRule.onNodeWithText(labels.getStarted).performClick()
        composeTestRule.waitForIdle()
        // From page 2 up to page 6 (Your History).
        repeat(4) { clickNext() }

        composeTestRule.onNodeWithText(labels.historyTitle).assertIsDisplayed()
        assertEquals(
            "The History page must not show the NEW badge",
            0,
            countDisplayedNodesWithText(labels.newBadge),
        )

        clickNext()

        composeTestRule.onNodeWithText(labels.tournamentTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(labels.tournamentSubtitle).assertIsDisplayed()
        assertEquals(
            "The Tournament page must show the NEW badge",
            1,
            countDisplayedNodesWithText(labels.newBadge),
        )
        // The feature rows sit at the bottom of a scrollable page, so reaching them proves the
        // page content still works inside the existing verticalScroll container.
        composeTestRule.onNodeWithText(labels.tournamentFeature3).performScrollTo().assertIsDisplayed()
    }
}
